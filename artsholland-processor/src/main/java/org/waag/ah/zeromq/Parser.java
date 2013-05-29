package org.waag.ah.zeromq;

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
import org.waag.ah.importer.ImportDocument;
import org.waag.ah.service.MongoConnectionService;
import org.waag.ah.tika.DocumentParser;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Startup
@Singleton
public class Parser {
	private static final Logger logger = LoggerFactory.getLogger(Parser.class);

//	public final static String SOCKET = "tcp://localhost:5558"; 
	
	private ParserThread parser;
	private ServerThread server;
	private Gson gson = new Gson();
	private BlockingQueue<BasicDBObject> queue;
	private DBCollection collection;
	
	private @EJB MongoConnectionService mongo;

	@PostConstruct
	public void listen() {
		collection = mongo.getCollection(Parser.class.getName());
		queue = new SynchronousQueue<BasicDBObject>(true);
		
		server = new ServerThread();
		server.start();
		
		parser = new ParserThread();
		parser.start();
	}
	
	@PreDestroy
	public void unlisten() {
		parser.interrupt();
		server.interrupt();
	}
	
	/**
	 * Parsed documents queue.
	 * 
	 * @author Raoul Wissink <raoul@raoul.net>
	 */
	private class ServerThread extends Thread {
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket sender = context.socket(ZMQ.PUSH);
			sender.setHWM(1);
	        sender.connect("tcp://localhost:5559");
	        
	        while (!Thread.currentThread().isInterrupted()) {
	        	try {
	        		// Wait for fetched batch.
	        		BasicDBObject batch = queue.take();
	        		BasicDBObject query = new BasicDBObject()
	        			.append("guid", batch.get("guid"))
	        			.append("status", null);
	        		
	        		BasicDBObject fields = new BasicDBObject("data", 1);
	        		
	        		DBCursor data = collection.find(query, fields);
	        		while (data.hasNext()) {
	        			DBObject doc = data.next();
	        			sender.send(gson.toJson(doc), data.hasNext() ? ZMQ.SNDMORE : 0);
	        		}
	        		
	        		// Remove all references to this batch from DB.
	        		collection.findAndRemove(new BasicDBObject("guid", batch.get("guid")));		        		
	        	} catch (Exception e) {
	        		logger.error(e.getMessage(), e);
	        	}
	        }

	        sender.close();
        	context.term();
		}
	}
	
	/**
	 * Document parser.
	 * 
	 * @author Raoul Wissink <raoul@raoul.net>
	 */
	private class ParserThread extends Thread {
//		private final Type type = new TypeToken<Collection<Document>>(){}.getType();
		private DocumentParser parser = new DocumentParser();
		
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket receiver = context.socket(ZMQ.PULL);
	        receiver.bind("tcp://*:5558");

	        while (!Thread.currentThread().isInterrupted()) {
	        	try {
	        		// Get unique ID for this job.
        	    	String guid = UUID.randomUUID().toString();

	        		boolean more = true;
	        		while (more) {
		        		String json = receiver.recvStr();
		        		ImportDocument doc = gson.fromJson(json, ImportDocument.class);
		        		String data = parser.parse(doc);
		        		
		        		BasicDBObject document = new BasicDBObject();
        	    		document.append("guid", guid);
        	    		document.append("data", data);
        	    		
        	    		collection.insert(document);
        	    		
        	    		logger.info("Parsed document: size="+data.length());
			        	
        	    		more = receiver.hasReceiveMore();
		        	}
        	    	
        	    	// Add batch reference to DB.
        	    	BasicDBObject batch = new BasicDBObject();
        	    	batch.append("guid", guid);
        	    	batch.append("status", true);
        	    	collection.insert(batch);

        	    	// Mark batch as finished.
        	    	queue.put(batch);	
		        } catch (Exception e) {
		        	logger.error(e.getMessage(), e);
		        }
	        }

	        receiver.close();
        	context.term();
	    }
	}
}
