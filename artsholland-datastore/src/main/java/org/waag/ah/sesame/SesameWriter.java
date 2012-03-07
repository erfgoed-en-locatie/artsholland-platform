package org.waag.ah.sesame;
//package org.waag.ah.sesame;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.Reader;
//import java.io.StringReader;
//import java.util.Map;
//
//import javax.naming.InitialContext;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.URI;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.repository.RepositoryConnection;
//import org.openrdf.repository.RepositoryException;
//import org.openrdf.rio.RDFFormat;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.waag.ah.DocumentWriter;
//import org.waag.ah.RepositoryConnectionFactory;
//import org.waag.ah.jms.Properties;
//
//public class SesameWriter implements DocumentWriter {
//	final static Logger logger = LoggerFactory.getLogger(SesameWriter.class);
////	private Logger logger = Logger.getLogger(SesameWriter.class);
//	private RepositoryConnection connection;
//	private ValueFactory valueFactory;
//
//	public SesameWriter() throws IOException {
//		try {
//			InitialContext context = new InitialContext();
//			RepositoryConnectionFactory connectionFactory = (RepositoryConnectionFactory) 
//					context.lookup("java:global/BigdataConnectionService");
//			connection = connectionFactory.getConnection();
//			valueFactory = connection.getValueFactory();
//			logger.info("Connected to repository");
//		} catch (Exception e) {
//			throw new IOException(e.getMessage(), e);
//		}
//	}
//	
//	public void close() throws IOException {
//		try {
//			connection.close();
//		} catch (IllegalMonitorStateException e) {
//			logger.warn("Repository connection already closed", e);
//		} catch (RepositoryException e) {
//			throw new IOException(e.getMessage(), e);
//		}
//	}
//
//	@Override
//	public void write(String message, Map<String, String> metadata) throws IOException {
//		String baseURL = metadata.get(Properties.SOURCE_URL);
//		URI context = valueFactory.createURI(baseURL);
////		logger.info("CONTEXT: "+context.stringValue());
////		try {
////		} catch (IllegalArgumentException e) {
////			throw new IOException("Empty or invalid resource URI provided.", e);
////		}
//
//		Reader reader = new StringReader(message);
//		try {
//			connection.add(reader, baseURL, RDFFormat.RDFXML, context);
//			connection.commit();
//			logger.info("After loading, repository contains " + connection.size(context) +
//	                " triples in context '" + context + "' and " +
//	                connection.size((Resource)null) + " triples in context 'null'.");
//		} catch (NullPointerException e) {
//			// Apparently, when running in "full feature" mode, BigData returns
//			// this exception if all provided triples already exist in the
//			// repository.
//			logger.debug("No new data found at resource "+baseURL);
//		} catch (Exception e) {
//			try {
//				connection.rollback();
//			} catch (RepositoryException e1) {
//				throw new IOException(e1.getMessage(), e1);
//			}
//			throw new IOException(e.getMessage(), e);
//		}
////		logger.info("Written RDF data");
//	}
//
//	@Override
//	public void write(InputStream inputStream, Map<String, String> metadata)
//			throws IOException {
//		throw new UnsupportedOperationException();
//	}
//}