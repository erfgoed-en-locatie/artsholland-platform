package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.StringReader;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.Depends;
import org.jboss.ejb3.annotation.Management;
import org.jboss.ejb3.annotation.Service;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.waag.ah.ServiceManagement;
import org.waag.ah.jms.Properties;
import org.waag.ah.persistence.RepositoryConnectionFactory;
import org.waag.ah.persistence.SAILConnectionFactory;

@Service(objectName = StorageQueueBean.OBJECT_NAME)
@Management(ServiceManagement.class)
@Depends(SAILConnectionFactory.OBJECT_NAME)
@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/store"),
		@ActivationConfigProperty(propertyName="messageSelector", propertyValue=Properties.CONTENT_TYPE+"='application/rdf+xml'")})
public class StorageQueueBean implements ServiceManagement {
	private Logger logger = Logger.getLogger(StorageQueueBean.class);
	private RDFXMLParser parser;
	private RDFStorageHandler handler;
	private RepositoryConnection connection;
	
	public final static String OBJECT_NAME = "artsholland:service=StorageService";

//	@PostConstruct
	@Override
	public void create() throws NamingException, RepositoryException, IOException {
		logger.info("Creating service "+OBJECT_NAME);
		InitialContext context = new InitialContext();
		RepositoryConnectionFactory cf = (RepositoryConnectionFactory) 
				context.lookup("java:global/"+SAILConnectionFactory.OBJECT_NAME);
		connection = cf.getConnection();		
		
//		System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
		handler = new RDFStorageHandler(connection);
		parser = new RDFXMLParser();
		parser.setRDFHandler(handler);
//		parser.setParseErrorListener(myParseErrorListener);
		parser.setVerifyData(true);
		parser.setStopAtFirstError(false);
	}
	
//	@PreDestroy
	@Override
	public void destroy() {
		logger.info("Closing connection"+connection);
		try {
			connection.close();
		} catch (IllegalMonitorStateException e) {
			logger.warn("Error closing repository connection: "+e.getMessage());
		} catch (RepositoryException e) {
			logger.warn("Error closing repository connection: "+e.getMessage());
		}
	}
	
	public void onMessage(Message msg) throws IOException, JMSException {
		String messageText = ((TextMessage)msg).getText();
		logger.info("Storing RDF document: size="+messageText.length());
		try {
			parser.parse(new StringReader(messageText), 
					msg.getStringProperty(Properties.SOURCE_URL));
			logger.info("Repository contains "+handler.connection.size()+" triples");
		} catch (OpenRDFException e) {
			logger.warn("Error parsing RDF document: "+e.getMessage());
		}
	}
	
	public void storeDocument(String document) {
	}

	private class RDFStorageHandler implements RDFHandler {
		private RepositoryConnection connection;

		public RDFStorageHandler(RepositoryConnection connection) {
			this.connection = connection;
		}

		@Override
		public void startRDF() throws RDFHandlerException {
		}
	
		@Override
		public void endRDF() throws RDFHandlerException {
		}
	
		@Override
		public void handleNamespace(String prefix, String uri)
				throws RDFHandlerException {
		}
	
		@Override
		public void handleStatement(Statement statement) throws RDFHandlerException {
//			logger.info("Storing statement: "+statement);
			try {
				connection.add(statement);
				connection.commit();
//				logger.info("Statement context: "+statement.getContext());
			} catch (RepositoryException e) {
				throw new RDFHandlerException(e.getMessage(), e);
			}
		}
	
		@Override
		public void handleComment(String comment) throws RDFHandlerException {
		}
	}
}