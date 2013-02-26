package org.waag.ah.zeromq;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.zeromq.ZMQ;

@Startup
@Singleton
public class Parser {
//	private static final Logger logger = LoggerFactory.getLogger(Parser.class);

	final String SOCKET = "tcp://localhost:5558"; 
	
	private ServerThread server;
//	private DBCollection collection;
	
//	private @EJB MongoConnectionService mongo;
	
	@PostConstruct
	public void listen() {
//		collection = mongo.getCollection(Parser.class.getName());
		server = new ServerThread();
		server.start();
	}
	
	@PreDestroy
	public void unlisten() {
		server.interrupt();
	}
	
	private class ServerThread extends Thread {
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket receiver = context.socket(ZMQ.PULL);
	        receiver.bind(Socket.FETCHER);
	        try {
		        while (true) {
		        	
		        }
	        } finally {
	        	receiver.close();
	        	context.term();
	        }
	    }
	}
}
