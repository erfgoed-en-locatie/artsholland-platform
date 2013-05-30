package org.waag.ah.zeromq;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.configuration.ConfigurationException;
import org.jeromq.ZMQ;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.importer.ImportDocument;
import org.waag.ah.service.RepositoryConnectionFactory;

import com.google.gson.Gson;

@Startup
@Singleton
public class Importer {
	private static final Logger logger = LoggerFactory.getLogger(Importer.class);
	
	private Gson gson = new Gson();
	private ImporterThread parser;
	private PlatformConfig config;

	private @EJB RepositoryConnectionFactory sesame;

	@PostConstruct
	public void listen() {
		try {
			config = PlatformConfigHelper.getConfig();
			parser = new ImporterThread(sesame);
			parser.start();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
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
//		private RDFParser rdfParser;
		
		public ImporterThread(RepositoryConnectionFactory sesame)
				throws RepositoryException {
			super();
			conn = sesame.getReadWriteConnection();
//			rdfParser = new TurtleParser();
//			rdfParser.setRDFHandler(new StatementAdder(conn));
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
		        		InputStream in = new ByteArrayInputStream(doc.getData().getBytes("UTF-8"));

		        		logger.info("IMPORTING: size="+doc.getData().length());

		        		conn.add(in, config.getString("platform.baseUri"), RDFFormat.TURTLE);
		        		
//		        		try {
//		        			rdfParser.parse(in, "http://artsholland.com");
//		        		} catch(RDFParseException e) {
//		        			logger.info(doc.getData());
//		        		} finally {
//		        			in.close();
//		        		}
			        	more = receiver.hasReceiveMore();
	        		}
		        	conn.commit();
		        	logger.info("SIZE AFTER: "+conn.size());
		        } catch (Exception e) {
		        	try {
						conn.rollback();
					} catch (Exception e1) { // IllegalStateException
						// Rollback only fails when the connection has died. 
						Thread.currentThread().interrupt();
					}
		        	logger.error(e.getMessage(), e);
		        }
	        }
	        
        	receiver.close();
        	context.term();
	    }
	}

//	private static class StatementAdder extends RDFHandlerBase {
//		private final RepositoryConnection repository;
//
//		public StatementAdder(RepositoryConnection repository) {
//			this.repository = repository;
//		}
//
//		@Override
//		public synchronized void handleStatement(Statement st) {
//			try {
//				repository.add(st);
//			} catch (RepositoryException e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
//	}
}
