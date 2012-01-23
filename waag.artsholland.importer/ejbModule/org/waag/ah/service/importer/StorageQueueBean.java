package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageListener;
import javax.jms.MessageNotReadableException;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.waag.ah.jms.Properties;
import org.waag.ah.persistence.RepositoryConnectionFactory;

@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/store"),
		@ActivationConfigProperty(propertyName="messageSelector", propertyValue=Properties.CONTENT_TYPE+"='application/rdf+xml'")})
public class StorageQueueBean {
	private Logger logger = Logger.getLogger(StorageQueueBean.class);
	private RDFXMLParser parser;
	private RDFStorageHandler handler;
	
	@PostConstruct
	public void create() throws Exception {
		InitialContext ctx = new InitialContext();
		RepositoryConnectionFactory cf = (RepositoryConnectionFactory) 
				ctx.lookup("java:global/SAILConnectionFactory");
		
		handler = new RDFStorageHandler(cf);
		parser = new RDFXMLParser();
		parser.setRDFHandler(handler);
		parser.setStopAtFirstError(false);
	}
	
	public void onMessage(Message msg) throws JMSException {
		try {
			String messageText = ((TextMessage)msg).getText();
			logger.info("Storing RDF document: size="+messageText.length()+", uri="+
					msg.getStringProperty(Properties.SOURCE_URL));
			parser.parse(new StringReader(messageText),
					msg.getStringProperty(Properties.SOURCE_URL));
		} catch (OpenRDFException e) {
			throw new MessageFormatException(e.getMessage());
		} catch (IOException e) {
			throw new MessageNotReadableException(e.getMessage());
		}
	}

	private class RDFStorageHandler extends RDFHandlerBase {
		private List<Statement> statements = new ArrayList<Statement>();
		private RepositoryConnectionFactory cf;

		public RDFStorageHandler(RepositoryConnectionFactory cf) {
			this.cf = cf;
		}
	
		@Override
		public void handleStatement(Statement statement) throws RDFHandlerException {
			statements.add(statement);
		}
		
		@Override
		public void endRDF() throws RDFHandlerException {
			RepositoryConnection conn;
			try {
				conn = cf.getConnection();
			} catch (Exception e) {
				throw new RDFHandlerException(e.getMessage(), e);
			}
			try {
				conn.add(statements);
				conn.commit();
				logger.info("Repository contains "+conn.size()+" triples");
			} catch (RepositoryException e) {
				logger.error("Exception while adding data to DB: message="+e.getMessage());
				try {
					conn.rollback();
				} catch (RepositoryException e1) {
					logger.warn("Execption while rolling back transaction: "+e1.getMessage());
//					throw new RDFHandlerException(e1.getMessage(), e1);
				}
				throw new RDFHandlerException(e.getMessage(), e);
			} finally {
				statements.clear();
				try {
					conn.close();
				} catch (RepositoryException e) {
					Log.warn("Error closing connection: "+e.getMessage());
				}
			}
		}
	}
}