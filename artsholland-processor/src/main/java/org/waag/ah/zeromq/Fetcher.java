package org.waag.ah.zeromq;

import java.net.URL;
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
	private BlockingQueue<DBObject> queue = new SynchronousQueue<DBObject>(true);

	@PostConstruct
	public void listen() {
		collection = mongo.getCollection(Fetcher.class.getName());
		
		// Start document server.
		server = new ServerThread();
		server.start();
		
		// Start URL fetcher process.
		fetcher = new FetcherThread();
		fetcher.start();
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
	        		DBObject batch = queue.take();
	        		
	        		// Send data and url field.
//	        		BasicDBObject fields = new BasicDBObject()
//	        			.append("data", 1)
//	        			.append("url", 1);
	        		
	        		DBCursor data = collection.find(new BasicDBObject()
	        			.append("jobId", batch.get("jobId"))
	        			.append("status", null));
	        		
	        		while (data.hasNext()) {
	        			DBObject doc = data.next();
	        			sender.send(gson.toJson(doc), data.hasNext() ? ZMQ.SNDMORE : 0);
	        			collection.remove(doc);
	        		}
	        		
	        		// Remove all references to this batch from DB.
//	        		collection.findAndRemove(new BasicDBObject("jobId", batch.get("jobId")));
	        		collection.remove(batch);
	        	} catch(Exception e) {
	        		logger.error(e.getMessage(), e);
	        	}
	        }
		}
	}
	
	private class FetcherThread extends Thread {
		@Override
		public void run() {
			// Load pending jobs (backlog).
//			try {
//				BasicDBObject query = new BasicDBObject("status", true);
//				DBCursor cursor = collection.find(query);
//				while (cursor.hasNext()) {
//					DBObject doc = cursor.next();
//					logger.info("Processing backlog: "+doc.get("jobId"));
//					queue.put(doc);
//				}
//			} catch (InterruptedException e) {
//				logger.error("Error processing backlog", e);
//			}
			
			// Listen for new jobs (after processing backlog).
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
            	    	String jobId = UUID.randomUUID().toString();
            	    	
            	    	// Fetch URLs and store content in DB.
	        	    	for (int i=0; i<urls.length; i++) {
	        	    		URL url = URLTools.getAuthenticatedUrl(urls[i]);
	        	    		String data = URLTools.getUrlContent(url);
	        	    		
	        	    		collection.insert(new BasicDBObject()
	        	    			.append("jobId", jobId)
	        	    			.append("url", url.toExternalForm())
	        	    			.append("data", data));
	        	    		
		        			logger.info("Fetched URL ("+(i+1)+"/"+urls.length+"): "+url);
		        		}
            	    	
            	    	// Add batch reference to DB.
            	    	BasicDBObject batch = new BasicDBObject();
            	    	batch.append("jobId", jobId);
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
	}
}
