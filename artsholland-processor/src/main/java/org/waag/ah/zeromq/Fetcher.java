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

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Startup
@Singleton
public class Fetcher {
	private static final Logger logger = LoggerFactory.getLogger(Fetcher.class);

	private FetcherThread fetcher;
	private ServerThread server;

	private DBCollection collection;
	private Gson gson = new Gson();

	private @EJB MongoConnectionService mongo;

	@PostConstruct
	public void listen() {
		collection = mongo.getCollection(Fetcher.class.getName());
		fetcher = new FetcherThread();
		fetcher.start();
		server = new ServerThread();
		server.start();
	}
	
	@PreDestroy
	public void unlisten() {
		fetcher.interrupt();
		server.interrupt();
	}
	
	private class ServerThread extends Thread {
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket sender = context.socket(ZMQ.PUSH);
			sender.setHWM(1);
	        sender.connect(Socket.FETCHER);
	        while (true) {
	        	try {
	        		DBCursor cursor = collection.find().sort(new BasicDBObject().append("_id", 1));
	        		while (cursor.hasNext()) {
		        		DBObject data = cursor.next();
		        		String json = gson.toJson(data.get("data"));
		        		logger.info("SENDING: "+json);
		        		if (sender.send(json)) {
		        			collection.remove(data);
		        		}
	        		}
        			Thread.sleep(100);
	        	} catch(Exception e) {
	        		logger.error(e.getMessage(), e);
	        	}
	        }
		}
	}
	
	private class FetcherThread extends Thread {
		private boolean running = true;
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket receiver = context.socket(ZMQ.PULL);
	        receiver.bind(Socket.FETCHER_SERVER_URL);
	        while (running) {
        		BasicDBObject doc = new BasicDBObject();
        	    try {
        	    	String[] urls = gson.fromJson(receiver.recvStr(), String[].class);
        	    	if (urls.length > 0) {
        	    		collection.insert(doc);	  
	        	    	for (int i=0; i<urls.length; i++) {
		        			URL url = getAuthenticatedUrl(urls[i]);
		        			BasicDBObject update = new BasicDBObject();
		        			update.put("$push", new BasicDBObject()
		        				.append("url", url.toString())
		        				.append("data", getUrlContent(url))
		        			);
		        			logger.info("FETCHED: "+url);
		        			collection.update(doc, update);
		        		}
        	    	}
		        } catch(Exception e) {
		        	collection.remove(doc);	 
		        	logger.error(e.getMessage());
		        }
	        }
        	receiver.close();
        	context.term();
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
