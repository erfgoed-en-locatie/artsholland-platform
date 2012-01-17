package org.waag.ah.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.waag.ah.service.importer.DocumentWriter;

public class SesameWriter implements DocumentWriter {
	private Logger logger = Logger.getLogger(SesameWriter.class);
	private RepositoryConnection connection;
	private ValueFactory valueFactory;

	public SesameWriter() throws IOException {
		try {
			InitialContext context = new InitialContext();
			RepositoryConnectionFactory connectionFactory = (RepositoryConnectionFactory) 
					context.lookup("java:global/SAILConnectionFactory");
			connection = connectionFactory.getConnection();
			valueFactory = connection.getValueFactory();
			logger.info("Connected to repository");
		} catch (Exception e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (IllegalMonitorStateException e) {
			logger.warn("Repository connection already closed", e);
		} catch (RepositoryException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public void write(String message, Metadata metadata) throws IOException {
		String baseURL = metadata.get(Metadata.RESOURCE_NAME_KEY);
		URI context = valueFactory.createURI(baseURL);
//		logger.info("CONTEXT: "+context.stringValue());
//		try {
//		} catch (IllegalArgumentException e) {
//			throw new IOException("Empty or invalid resource URI provided.", e);
//		}

		Reader reader = new StringReader(message);
		try {
			connection.add(reader, baseURL, RDFFormat.RDFXML, context);
			connection.commit();
			logger.info("After loading, repository contains " + connection.size(context) +
	                " triples in context '" + context + "'\n    and   " +
	                connection.size((Resource)null) + " triples in context 'null'.");
		} catch (NullPointerException e) {
			// Appearantly, when running in "full feature" mode, BigData returns
			// this exception if all provided triples already exist in the
			// repository.
			logger.debug("No new data found at resource "+baseURL);
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (RepositoryException e1) {
				throw new IOException(e1.getMessage(), e1);
			}
			throw new IOException(e.getMessage(), e);
		}
		logger.info("Written RDF data");
	}
}