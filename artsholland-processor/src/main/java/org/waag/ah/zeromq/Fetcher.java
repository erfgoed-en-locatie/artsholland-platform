package org.waag.ah.zeromq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PasswordAuthenticator;
import org.waag.ah.mongo.PersistentQueue;
import org.waag.ah.service.MongoConnectionService;
import org.zeromq.ZMQ;

import com.google.gson.Gson;
import com.mongodb.DBCollection;

@Startup
@Singleton
public class Fetcher {
	private static final Logger logger = LoggerFactory.getLogger(Fetcher.class);

//	public final static String SOCKET = "tcp://localhost:5558"; 
	
	private FetcherThread fetcher;
	private ServerThread server;

	private Gson gson = new Gson();
	private DBCollection collection;

	private @EJB MongoConnectionService mongo;
	
	private PersistentQueue<Map<String, String>> queue;


	@PostConstruct
	public void listen() {
		collection = mongo.getCollection(Fetcher.class.getName());
		queue = new PersistentQueue<Map<String, String>>(collection);
		
		// Start URL fetcher process.
		fetcher = new FetcherThread();
		fetcher.start();
		
		// Start docuement server.
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
	        sender.connect("tcp://localhost:5558");
	        while (true) {
	        	try {
	        		sender.send(gson.toJson(queue.get()));
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
	        receiver.bind("tcp://*:5557");
	        while (running) {
        	    try {
        	    	String[] urls = gson.fromJson(receiver.recvStr(), String[].class);
        	    	if (urls.length > 0) {
	        	    	for (int i=0; i<urls.length; i++) {
	        	    		URL url = getAuthenticatedUrl(urls[i]);
	        	    		queue.put(getDocument(url));
		        			logger.info("Fetched URL: "+url);
		        		}
	        	    	queue.commit();
        	    	}
		        } catch(Exception e) {
		        	logger.error(e.getMessage());
		        	queue.rollback();
		        }
	        }
        	receiver.close();
        	context.term();
	    }
		
		private Map<String, String> getDocument(URL url) throws IOException {
    		Map<String, String> document = new HashMap<String, String>();
    		document.put("url", url.toString());
    		document.put("data", getUrlContent(url));
    		return document;
		}
	}
	
	public static URL getAuthenticatedUrl(String url) throws MalformedURLException {
		return getAuthenticatedUrl(new URL(url));
	}
	
	public static URL getAuthenticatedUrl(URL url) throws MalformedURLException {
		boolean authenticated = false;
		String username = "";
		String password = "";
		
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
