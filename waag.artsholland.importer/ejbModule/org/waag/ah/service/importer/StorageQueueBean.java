package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.Depends;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.waag.ah.jms.Properties;
import org.waag.ah.jms.StreamingMessageBuffer;
import org.waag.ah.persistence.RepositoryConnectionFactory;
import org.waag.ah.persistence.SAILConnectionFactory;

@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/store"),
		@ActivationConfigProperty(propertyName="messageSelector", propertyValue=Properties.CONTENT_TYPE+"='application/rdf+xml'"),
		@ActivationConfigProperty(propertyName="maxSession", propertyValue = "1"),
		@ActivationConfigProperty(propertyName="transactionTimeout", propertyValue="900000")})
@Depends(SAILConnectionFactory.OBJECT_NAME)
public class StorageQueueBean implements MessageListener {
	private Logger logger = Logger.getLogger(StorageQueueBean.class);
	private RDFXMLParser parser;
	private RDFStorageHandler handler;
	private RepositoryConnection conn;
	
	@EJB(mappedName = "java:global/SAILConnectionFactory")
	private RepositoryConnectionFactory cf;
	
	private @EJB StreamingMessageBuffer streamHelper;
	
	@PostConstruct
	public void create() throws Exception {
		conn = cf.getConnection();
		handler = new RDFStorageHandler(conn);
		parser = new RDFXMLParser();
		parser.setRDFHandler(handler);
	}
	
	@Override
//	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) 
//	@TransactionTimeout(value=60000*15)
	public void onMessage(Message message) {
		PipedInputStream in = null;
		PipedOutputStream out = null;
		try {
			in = new PipedInputStream();
			out = new PipedOutputStream(in);

			logger.info("STORING DOCUMENT");
			long size = conn.size();
			streamHelper.pipedReader((BytesMessage) message, out);
			logger.info("STARTED READING INPUTSTREAM");
			parser.parse(in, message.getStringProperty(Properties.SOURCE_URL));
			logger.info("Stored RDF document: triples="+(conn.size()-size));
		} catch(Exception e) {
			logger.error("Error while processing message: "+e.getMessage());
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		logger.info("FINISHED STORING DOCUMENT");
	}
	
	private class RDFStorageHandler extends RDFHandlerBase {
		private Logger logger = Logger.getLogger(RDFStorageHandler.class);
		private RepositoryConnection conn;
		private int counter = 0;

		public RDFStorageHandler(RepositoryConnection conn) {
			this.conn = conn;
		}
		
		@Override
		public void handleStatement(Statement statement) throws RDFHandlerException {
			try {
				conn.add(statement);
				counter++;
				if (counter % 1024 == 0) {
					logger.info("ADDED "+counter+" STATEMENTS");
				}
			} catch (RepositoryException e) {
				rollback(e);
			}
		}
		
		@Override
		public void startRDF() throws RDFHandlerException {
			counter = 0;
		}

		@Override
		public void endRDF() throws RDFHandlerException {
			try {
				logger.info("COMMITTING "+counter+" STATEMENTS");
				conn.commit();
			} catch (RepositoryException e) {
				rollback(e);
			}
		}

		private void rollback(Exception e) throws RDFHandlerException {
			logger.error(e.getMessage());
			try {
				conn.rollback();
			} catch (RepositoryException e1) {
				logger.warn("Execption while rolling back transaction: "+e1.getMessage());
			}
			throw new RDFHandlerException("ROLLED BACK TRANSACTION");
		}
	}
}