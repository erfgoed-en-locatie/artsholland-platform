package org.waag.ah.mongo;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PersistentQueue {
	private static final Logger logger = LoggerFactory.getLogger(PersistentQueue.class);
	
	private static final int COMPLETED = 1;
	private static final int PROCESSING = 2;
	
	private DBCollection collection;
	private QueryThread queryThread;
	private ArrayBlockingQueue<DBObject> buffer;

	private BasicDBObject document = new BasicDBObject();
	private DBObject current;

	private DBObject status_copmpleted = new BasicDBObject("status", COMPLETED);
	private DBObject status_processing = new BasicDBObject("status", PROCESSING);

	public PersistentQueue(DBCollection collection) {
		this.collection = collection;
		this.buffer = new ArrayBlockingQueue<DBObject>(1);
		this.queryThread = new QueryThread();
		this.queryThread.start();			
	}
	
	@SuppressWarnings("unchecked")
	public List<String> get() throws InterruptedException {
		synchronized (buffer) {
			if (current != null) {
				try {
					// Assuming the previous item was succesfulle processed.
					logger.info("Removing current buffer item");
					collection.remove(current);
				} finally {
					current = null;
				}					
			}
			current = buffer.take();
			logger.info("Sending next queued document");
			return (List<String>) current.get("data");
		}
	}

	public void put(String object) {
		synchronized (document) {
			if (document.isEmpty()) {
				logger.info("Creating document container");
				collection.insert(document);
			}
			logger.info("Adding document to container");
			BasicDBObject insert = new BasicDBObject().append("data", object);
			collection.update(document, new BasicDBObject("$push", new BasicDBObject("data", insert)));				
		}
	}
	
	public void commit() {
		synchronized (document) {
			if (document.isEmpty()) {
				throw new UnsupportedOperationException("No pending transaction");
			}
			collection.update(document, new BasicDBObject("$set", status_copmpleted));
			document = new BasicDBObject();
		}
	}
	
	public void rollback() {
		synchronized (document) {
			try {
				collection.remove(document);
			} finally {
				document = new BasicDBObject();
			}
		}
	}
	
	private class QueryThread extends Thread {
		@Override
		public void run() {
			DBObject sort = new BasicDBObject().append("_id", 1);
	        while (true) {
	        	try {
	        		DBCursor cursor = collection.find(status_copmpleted).sort(sort).limit(1);
	        		if (cursor.hasNext()) {
	        			synchronized (document) {
		        			logger.info("Queueing document");
		        			collection.update(document, new BasicDBObject("$set", status_processing));
		    				buffer.put(cursor.next());
	        			}
	        		}
	        		Thread.sleep(10);
	        	} catch (Exception e) {
	        		logger.error(e.getMessage(), e);
	        	}
	        }
		}
	}
}
