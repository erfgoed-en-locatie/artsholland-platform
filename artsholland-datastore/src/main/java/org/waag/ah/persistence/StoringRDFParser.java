package org.waag.ah.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.RepositoryConnectionFactory;

@Stateful
public class StoringRDFParser extends RDFXMLParser {
	final static Logger logger = LoggerFactory.getLogger(StoringRDFParser.class);
	private RepositoryConnection conn;
	private ValueFactory vf;
	private URI baseURI;
	private URI source;

	@EJB(lookup="java:module/SAILConnectionFactory")
	private RepositoryConnectionFactory cf;
	
	@PostConstruct
	public void connect() {
		try {
			conn = cf.getConnection();
			setRDFHandler(new RDFHandler());
			vf = conn.getValueFactory();
			source = vf.createURI("http://purl.org/artsholland/1.0/metadata/source");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	@Override
	public synchronized void parse(InputStream in, String baseURI)
			throws IOException, RDFParseException, RDFHandlerException {
		this.baseURI = vf.createURI(baseURI);
		try {
			long cursize = conn.size();
			super.parse(in, baseURI);
			logger.info("ADDED "+(conn.size()-cursize)+" NEW STATEMENTS");
		} catch (RepositoryException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public synchronized void parse(Reader reader, String baseURI)
			throws IOException, RDFParseException, RDFHandlerException {
		this.baseURI = vf.createURI(baseURI);
		super.parse(reader, baseURI);
	}

	public void cancel() {
		try {
			logger.info("CANCEL REQUESTED!");
			conn.rollback();
		} catch (RepositoryException e) {
			logger.warn("Error rolling back transaction: "+e.getMessage());
		}
	}
	
	private class RDFHandler extends RDFHandlerBase {
		private int counter = 0;
		
		@Override
		public void handleStatement(Statement st) throws RDFHandlerException {
			try {
				Statement statement = getContextStatement(st);
				conn.add(statement);
				conn.add(statement.getContext(), source, baseURI);
				counter++;
				if (counter % 1024 == 0) {
					logger.info("ADDING "+counter+" STATEMENTS (CTX: "+statement.getContext()+")");
				}
			} catch (RepositoryException e) {
				try {
					conn.rollback();
				} catch (RepositoryException e1) {
					throw new RDFHandlerException(e);
				}
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
				try {
					conn.rollback();
				} catch (RepositoryException e1) {
					throw new RDFHandlerException(e);
				}
			}
		}
		
		private Statement getContextStatement(Statement st) {
			return vf.createStatement(st.getSubject(), st.getPredicate(), 
					st.getObject(), vf.createBNode());
		}
	}
}
