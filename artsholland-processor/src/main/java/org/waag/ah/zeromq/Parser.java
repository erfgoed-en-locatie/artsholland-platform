package org.waag.ah.zeromq;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.mongo.PersistentQueue;
import org.waag.ah.service.MongoConnectionService;
import org.waag.ah.tinkerpop.TikaParserPipe;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.DBCollection;
import com.tinkerpop.pipes.util.Pipeline;

@Startup
@Singleton
public class Parser {
	private static final Logger logger = LoggerFactory.getLogger(Parser.class);

//	public final static String SOCKET = "tcp://localhost:5558"; 
	
	private ParserThread parser;
	private ServerThread server;
	private Gson gson = new Gson();
	private PersistentQueue<String> queue;
	private DBCollection collection;
	
	private @EJB MongoConnectionService mongo;

	@PostConstruct
	public void listen() {
		collection = mongo.getCollection(Parser.class.getName());
		queue = new PersistentQueue<String>(collection);
		
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
	        try {
		        while (true) {
		        	try {
		        		sender.send(gson.toJson(queue.get()));
		        	} catch (Exception e) {
		        		logger.error(e.getMessage(), e);
		        	}
		        }
	        } finally {
	        	sender.close();
	        	context.term();
	        }
		}
	}
	
	/**
	 * Document parser.
	 * 
	 * @author Raoul Wissink <raoul@raoul.net>
	 */
	private class ParserThread extends Thread {
		private Pipeline<Document, String> pipeline = new ParserPipeline();
		private final Type type = new TypeToken<Collection<Document>>(){}.getType();
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket receiver = context.socket(ZMQ.PULL);
	        receiver.bind("tcp://*:5558");
	        try {
		        while (true) {
		        	try {
			        	Collection<Document> docs = gson.fromJson(receiver.recvStr(), type);	
			        	pipeline.setStarts(docs.iterator());
			        	while (pipeline.hasNext()) {
			        		queue.put(pipeline.next());
			        	}			     
			        	queue.commit();
			        } catch (Exception e) {
			        	logger.error(e.getMessage(), e);
			        	queue.rollback();
			        }
		        }
	        } finally {
	        	receiver.close();
	        	context.term();
	        }
	    }
	}
	
	public static class Document {
		private URL url;
		private String data;
		public URL getUrl() {
			return url;
		}
		protected String getData() {
			return data;
		}
		@Override
		public String toString() {
			return getData();
		}
	}
	
	/**
	 * Parser pipeline assembly.
	 * 
	 * @author Raoul Wissink <raoul@raoul.net>
	 */
	private static class ParserPipeline extends Pipeline<Document, String> {
		public ParserPipeline() {
			super();
			this.addPipe(new TikaParserPipe());
		}
	}
}
