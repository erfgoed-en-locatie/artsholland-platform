package org.waag.ah.zeromq;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jeromq.ZMQ;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.service.RepositoryConnectionFactory;

import com.google.gson.Gson;

@Startup
@Singleton
public class Importer {
	private static final Logger logger = LoggerFactory.getLogger(Importer.class);
	
	private Gson gson = new Gson();
	private ImporterThread parser;
	private RepositoryConnection repository;
	
	private @EJB RepositoryConnectionFactory sesame;

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
	    		repository = sesame.getReadWriteConnection(); 
        		RDFParser rdfParser = new RDFXMLParser();
//        		RDFHandler collector = new StatementCounter(repository);
        		rdfParser.setRDFHandler(new StatementAdder(repository));
		        while (true) {
		        	try {
			        	ArrayList<?> data = gson.fromJson(receiver.recvStr(), ArrayList.class);
		        		logger.info("SIZE BEFORE: "+repository.size());
			        	for (Object string : data) {
			        		logger.info("IMPORTING: size="+string.toString().length());
//				        	logger.info(string.toString());
			        		InputStream in = new ByteArrayInputStream(string.toString().getBytes("UTF-8"));
			        		try {
			        			rdfParser.parse(in, "http://artsholland.com");
			        		} catch(RDFParseException e) {
			        			logger.info(string.toString());
			        		}
			        	}
			        	logger.info("COMMITTING...");
			        	repository.commit();
			        	logger.info("SIZE AFTER: "+repository.size());
			        } catch (Exception e) {
			        	repository.rollback();
			        	logger.error(e.getMessage(), e);
			        }
		        }
	        } catch (RepositoryException e) {
	        	logger.error(e.getMessage(), e);
			} finally {
	        	receiver.close();
	        	context.term();
	        }
	    }
	}

	private static class StatementAdder extends RDFHandlerBase {
		private final RepositoryConnection repository;
//		private final int buffer;
//		private int counter;

		public StatementAdder(RepositoryConnection repository) {
			this.repository = repository;
//			this.buffer = buffer;
		}

		@Override
		public synchronized void handleStatement(Statement st) {
			try {
//				logger.info("Adding statement: "+st.toString());
//				int counter = 0;
				repository.add(st);
//        		if (counter > 1000) {
//        			logger.info("BETWEEN COMMITTING...");
//        			repository.commit();
//        			counter = 0;
//        		} else {
//        			counter++;
//        		}
			} catch (RepositoryException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
