package org.waag.ah.zeromq;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
import org.openrdf.rio.turtle.TurtleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.importer.ImportDocument;
import org.waag.ah.service.RepositoryConnectionFactory;

import com.google.gson.Gson;

@Startup
@Singleton
public class Importer {
	private static final Logger logger = LoggerFactory.getLogger(Importer.class);
	
	private Gson gson = new Gson();
	private ImporterThread parser;
	
	private @EJB RepositoryConnectionFactory sesame;

	@PostConstruct
	public void listen() {
		try {
			parser = new ImporterThread(sesame);
			parser.start();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void unlisten() {
		parser.interrupt();
	}
	
	private class ImporterThread extends Thread {
//		private final Type type = new TypeToken<Collection<Document>>(){}.getType();
		private RepositoryConnection conn;
		private RDFParser rdfParser;
		
		public ImporterThread(RepositoryConnectionFactory sesame)
				throws RepositoryException {
			super();
			conn = sesame.getReadWriteConnection();
			rdfParser = new TurtleParser();
			rdfParser.setRDFHandler(new StatementAdder(conn));
		}
		
		@Override
		public void run() {
			ZMQ.Context context = ZMQ.context(1);
			ZMQ.Socket receiver = context.socket(ZMQ.PULL);
	        receiver.bind("tcp://*:5559");
	        while (!Thread.currentThread().isInterrupted()) {
	        	boolean more = true;
	        	try {
	        		while (more) {
		        		ImportDocument doc = gson.fromJson(receiver.recvStr(), ImportDocument.class);	
		        		logger.info("IMPORTING: size="+doc.getData().length());
		        		InputStream in = new ByteArrayInputStream(doc.getData().getBytes("UTF-8"));
		        		try {
		        			rdfParser.parse(in, "http://artsholland.com");
		        		} catch(RDFParseException e) {
		        			logger.info(doc.getData());
		        		} finally {
		        			in.close();
		        		}
			        	more = receiver.hasReceiveMore();
	        		}
	        		logger.info("SIZE BEFORE: "+conn.size());
//			        	logger.info("COMMITTING...");
		        	conn.commit();
		        	logger.info("SIZE AFTER: "+conn.size());
		        } catch (Exception e) {
		        	try {
						conn.rollback();
					} catch (RepositoryException e1) {}
		        	logger.error(e.getMessage(), e);
		        }
	        }
        	receiver.close();
        	context.term();
	    }
	}

	private static class StatementAdder extends RDFHandlerBase {
		private final RepositoryConnection repository;

		public StatementAdder(RepositoryConnection repository) {
			this.repository = repository;
		}

		@Override
		public synchronized void handleStatement(Statement st) {
			try {
				repository.add(st);
			} catch (RepositoryException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
