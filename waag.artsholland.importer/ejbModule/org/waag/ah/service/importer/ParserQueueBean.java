package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.jboss.logging.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.waag.ah.persistence.RepositoryConnectionFactory;
import org.xml.sax.ContentHandler;

import com.gc.iotools.stream.is.InputStreamFromOutputStream;

@MessageDriven(
	activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/parse"),
		@ActivationConfigProperty(propertyName="maxSession", propertyValue = "1")})
//@TransactionManagement(value=TransactionManagementType.BEAN) 
//@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) 
public class ParserQueueBean implements MessageListener {
	private static final Logger logger = Logger.getLogger(ParserQueueBean.class.toString());
	private RDFXMLParser parser;
	private RDFStorageHandler handler;
	private RepositoryConnection conn;
	
	@EJB(lookup="java:app/waag.artsholland.datastore/SAILConnectionFactory")
	private RepositoryConnectionFactory cf;
	
	@PostConstruct
	public void create() {
		try {
			conn = cf.getConnection();
			handler = new RDFStorageHandler(conn);
			parser = new RDFXMLParser();
			parser.setRDFHandler(handler);	
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void onMessage(Message msgIn) {
		InputStream stream = null;
		try {
			URL uri = new URL(((TextMessage)msgIn).getText());
			stream = parse(uri);
			parser.parse(stream, uri.toExternalForm());
		} catch (Exception e) {
			try {
				handler.rollback(e);
			} catch (RDFHandlerException e1) {
				logger.error(e.getMessage());
			}
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	private InputStream parse(final URL uri) throws IOException {
		final InputStreamFromOutputStream<String> isos = new InputStreamFromOutputStream<String>() {
			@Override
			public String produce(final OutputStream outStream) throws Exception {
				URLConnection conn = uri.openConnection();
				InputStream inStream = conn.getInputStream();				
				
				logger.info("STARTED PARSING");
				AutoDetectParser parser = new AutoDetectParser();
					
				Metadata metadata = new Metadata();
				metadata.add(Metadata.RESOURCE_NAME_KEY, uri.toExternalForm());	
				metadata.add(Metadata.CONTENT_ENCODING,
						new InputStreamReader(inStream).getEncoding());
					
				ContentHandler handler = new ToXMLContentHandler(outStream, "UTF-8"); 
//							metadata.get(Metadata.CONTENT_ENCODING));
				
				parser.parse(inStream, handler, metadata, new ParseContext());
				logger.info("FINISHED PARSING");
				return "OK";
			}
		};
		return isos;
	}
	
	private static class RDFStorageHandler extends RDFHandlerBase {
		private RepositoryConnection conn;
		private ValueFactory vf;
		private int counter = 0;

		public RDFStorageHandler(RepositoryConnection conn) {
			this.conn = conn;
			this.vf = conn.getValueFactory();
		}
		
		@Override
		public void handleStatement(Statement st) throws RDFHandlerException {
			try {
				Statement statement = getContextStatement(st);
				conn.add(statement);
				counter++;
				if (counter % 1024 == 0) {
					logger.info("ADDED "+counter+" STATEMENTS (ID: "+statement.getContext()+")");
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
		
		private Statement getContextStatement(Statement st) {
			return vf.createStatement(st.getSubject(), st.getPredicate(), 
					st.getObject(), vf.createBNode());
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