package org.waag.ah.zeromq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PasswordAuthenticator;
import org.waag.ah.service.MongoConnectionService;
import org.zeromq.ZMQ;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

@Startup
@Singleton
public class UrlFetcher {
	private static final Logger logger = LoggerFactory.getLogger(UrlFetcher.class);

	private ServerThread server;
	private DBCollection collection;

	private @EJB MongoConnectionService mongo;
	
	@PostConstruct
	public void listen() {
		collection = mongo.getCollection(UrlFetcher.class.getName());
		server = new ServerThread();
		server.start();
	}
	
	@PreDestroy
	public void unlisten() {
		server.interrupt();
	}
	
	private class ServerThread extends Thread {
		private boolean running = true;
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket receiver = context.socket(ZMQ.REP);
	        receiver.bind("tcp://*:5557");
	        while (running) {
        		BasicDBObject doc = new BasicDBObject();
        	    collection.insert(doc);	  
        	    try {
	        		boolean more = true;
	        		while (more) {
	        			URL url = getAuthenticatedUrl(new String(receiver.recv(0)).trim());
	        			logger.info("INCOMING: "+url.toString());
	        			BasicDBObject updateCommand = new BasicDBObject();
	        			updateCommand.put("$push", new BasicDBObject()
	        				.append("url", url.toString())
	        				.append("data", getUrlContent(url))
	        			);
	        			collection.update(doc, updateCommand);
	        			more = receiver.hasReceiveMore();
	        		}
        			receiver.send("BAM!".getBytes(), 0);
		            logger.info("STORED: "+doc.size()+" URLs");
		        } catch(Exception e) {
		        	collection.remove(doc);	 
		        	logger.error(e.getMessage());
		        	receiver.send(("BAH!: "+e.getMessage()).getBytes(), 0);
		        }
	        }
//        	receiver.close();
//        	context.term();
	    }
	}
	
	private URL getAuthenticatedUrl(String fetchUrl) throws MalformedURLException {
		boolean authenticated = false;
		String username = "";
		String password = "";
		URL url = new URL(fetchUrl);
		
		String userInfo = url.getUserInfo();
		if (userInfo != null) {
			String[] usernamePassword = userInfo.split(":");
			if (usernamePassword.length == 2) {
				username = usernamePassword[0];
				password = usernamePassword[1];
				authenticated = true;
			}
		}		
		
		if (authenticated) {
			Authenticator.setDefault(new PasswordAuthenticator(username, password));
		} else {
			Authenticator.setDefault(null);
		}	
		
		return url;
	}
	
	private String getUrlContent(URL url) throws IOException {
		StringBuilder content = new StringBuilder();
		URLConnection conn = url.openConnection();	
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		return content.toString();
	}
}
