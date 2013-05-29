package org.waag.ah.zeromq;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.service.MongoConnectionService;
import org.waag.ah.util.URLTools;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Startup
@Singleton
public class Fetcher {
	private static final Logger logger = LoggerFactory.getLogger(Fetcher.class);

//	public final static String SOCKET = "tcp://localhost:5558"; 
	
	private FetcherThread fetcher;
	private ServerThread server;

	private @EJB MongoConnectionService mongo;

	private Gson gson = new Gson();
	private DBCollection collection;
	private BlockingQueue<BasicDBObject> queue = new SynchronousQueue<BasicDBObject>(true);

	@PostConstruct
	public void listen() {
		collection = mongo.getCollection(Fetcher.class.getName());
		
		// Start URL fetcher process.
		fetcher = new FetcherThread();
		fetcher.start();
		
		// Start document server.
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
	        		// Wait for available batch.
	        		BasicDBObject batch = queue.take();
	        		BasicDBObject query = new BasicDBObject()
	        			.append("guid", batch.get("guid"))
	        			.append("status", null);
	        		
	        		// Send data and url field.
	        		BasicDBObject fields = new BasicDBObject()
	        			.append("data", 1)
	        			.append("url", 1);
	        		
	        		DBCursor data = collection.find(query, fields);
	        		while (data.hasNext()) {
	        			DBObject doc = data.next();
	        			sender.send(gson.toJson(doc), data.hasNext() ? ZMQ.SNDMORE : 0);
	        		}
	        		
	        		// Remove all references to this batch from DB.
	        		collection.findAndRemove(new BasicDBObject("guid", batch.get("guid")));
	        	} catch(Exception e) {
	        		logger.error(e.getMessage(), e);
	        	}
	        }
		}
	}
	
	private class FetcherThread extends Thread {
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket receiver = context.socket(ZMQ.PULL);
	        receiver.bind("tcp://*:5557");
	        
	        while (!Thread.currentThread().isInterrupted()) {
        	    try {
        	    	// Wait for list of URLs (import job). 
        	    	String[] urls = gson.fromJson(receiver.recvStr(), String[].class);
        	    	
        	    	// Fetch URLs and store them in DB.
        	    	if (urls.length > 0) {
            	    	
            	    	// Get unique ID for this job.
            	    	String guid = UUID.randomUUID().toString();
            	    	
	        	    	for (int i=0; i<urls.length; i++) {
	        	    		BasicDBObject document = new BasicDBObject();
	        	    		Map<String, String> data = getDocument(URLTools.getAuthenticatedUrl(urls[i]));
	        	    		document.append("guid", guid);
	        	    		document.append("url", data.get("url"));
	        	    		document.append("data", data.get("data"));
		        			logger.info("Fetched URL ("+(i+1)+"/"+urls.length+"): "+data.get("url"));
		        			collection.insert(document);
		        		}
            	    	
            	    	// Add batch reference to DB.
            	    	BasicDBObject batch = new BasicDBObject();
            	    	batch.append("guid", guid);
            	    	batch.append("status", true);
            	    	collection.insert(batch);
            	    	
            	    	// Make batch available to server.
            	    	queue.put(batch);
        	    	}
		        } catch(Exception e) {
		        	logger.error(e.getMessage());
		        }
	        }
	        
        	receiver.close();
        	context.term();
	    }
		
		private Map<String, String> getDocument(URL url) throws IOException {
    		Map<String, String> document = new HashMap<String, String>();
    		document.put("url", url.toString());
    		document.put("data", URLTools.getUrlContent(url));
    		return document;
		}
	}
}
