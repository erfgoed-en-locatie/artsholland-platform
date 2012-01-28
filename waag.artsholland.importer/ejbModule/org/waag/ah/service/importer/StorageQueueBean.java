package org.waag.ah.service.importer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.Depends;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.waag.ah.jms.Properties;
import org.waag.ah.persistence.RepositoryConnectionFactory;
import org.waag.ah.persistence.SAILConnectionFactory;

@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/store"),
		@ActivationConfigProperty(propertyName="messageSelector", propertyValue=Properties.CONTENT_TYPE+"='application/rdf+xml'")})
@Depends(SAILConnectionFactory.OBJECT_NAME)
public class StorageQueueBean implements MessageListener {
	private Logger logger = Logger.getLogger(StorageQueueBean.class);
	private RDFXMLParser parser;
	private RDFStorageHandler handler;
	private RepositoryConnection conn;
	
	@EJB(mappedName = "java:global/SAILConnectionFactory")
	private RepositoryConnectionFactory cf;
	
	@PostConstruct
	public void create() throws Exception {
		conn = cf.getConnection();
		handler = new RDFStorageHandler(conn);
		parser = new RDFXMLParser();
		parser.setRDFHandler(handler);
	}
	
//	@PreDestroy
//	public void destroy() {
//		logger.info("Closing connection: "+conn);
//		try {
//			conn.close();
//		} catch (Exception e) {
//			logger.error("Error closing repository connection", e);
//		}
//	}
	
	@Override
	public void onMessage(Message msg) {
		try {
			String messageText = ((TextMessage)msg).getText();
			parser.parse(new StringReader(messageText),
					msg.getStringProperty(Properties.SOURCE_URL));
//			logger.info("Stored RDF document: size="+messageText.length());//+", uri="+
//					msg.getStringProperty(Properties.SOURCE_URL)+", triples="+conn.size());
//			msg.acknowledge();
		} catch(Exception e) {
			logger.error("Error while processing message: "+e.getMessage());
//			msg.setJMSRedelivered()
//		} catch (OpenRDFException e) {
//			throw new MessageFormatException(e.getMessage());
//		} catch (IOException e) {
//			throw new MessageNotReadableException(e.getMessage());
//		} catch (JMSException e) {
//			e.printStackTrace();
		}
	}

	private class RDFStorageHandler extends RDFHandlerBase {
		private Logger logger = Logger.getLogger(RDFStorageHandler.class);
		private List<Statement> statements = new ArrayList<Statement>();
		private RepositoryConnection conn;

		public RDFStorageHandler(RepositoryConnection conn) {
			this.conn = conn;
		}
		
		@Override
		public void handleStatement(Statement statement) throws RDFHandlerException {
			statements.add(statement);
		}
		
		@Override
		public void startRDF() throws RDFHandlerException {
			statements.clear();
		}
		
		@Override
		public void endRDF() throws RDFHandlerException {
//			try {
//				conn.add(statements);
//				conn.commit();
//			} catch (RepositoryException e) {
//				logger.error("Exception while adding data to repository: message="+e.getMessage());
//				try {
//					conn.rollback();
//				} catch (RepositoryException e1) {
//					logger.warn("Execption while rolling back transaction: "+e1.getMessage());
//				}
//				throw new RDFHandlerException(e.getMessage(), e);
//			} finally {
//				statements.clear();
//			}
		}
	}
}