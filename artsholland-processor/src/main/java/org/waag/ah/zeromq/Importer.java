package org.waag.ah.zeromq;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import com.google.gson.Gson;

@Startup
@Singleton
public class Importer {
	private static final Logger logger = LoggerFactory.getLogger(Importer.class);
	
	private Gson gson = new Gson();
	private ImporterThread parser;

	@PostConstruct
	public void listen() {
		parser = new ImporterThread();
		parser.start();
	}
	
	@PreDestroy
	public void unlisten() {
		parser.interrupt();
	}
	
	private class ImporterThread extends Thread {
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket receiver = context.socket(ZMQ.PULL);
	        receiver.bind("tcp://*:5559");
	        try {
		        while (true) {
		        	try {
			        	ArrayList<?> data = gson.fromJson(receiver.recvStr(), ArrayList.class);
			        	for (Object string : data) {
			        		logger.info("IMPORTING: size="+string.toString().length());
//			        		logger.info(XmlFormatter.format(string.toString()));
			        	}
			        } catch (Exception e) {
			        	logger.error(e.getMessage(), e);
			        }
		        }
	        } finally {
	        	receiver.close();
	        	context.term();
	        }
	    }
	}
}
