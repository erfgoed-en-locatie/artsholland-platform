package org.waag.ah.sesame;

import java.io.IOException;
import java.io.InputStream;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.waag.ah.ImportMetadata;

public class StoringRDFParser {
	final static Logger logger = LoggerFactory.getLogger(StoringRDFParser.class);
	private ValueFactory vf;
	private URI source;
	
	public StoringRDFParser() {
		this.vf = ValueFactoryImpl.getInstance();
		this.source = new URIImpl("http://purl.org/artsholland/1.0/metadata/source");
	}
	
	public void parse(RepositoryConnection conn, InputStream in, ImportMetadata metadata)
			throws RDFParseException, RDFHandlerException, IOException,
			RepositoryException {
		Assert.notNull(metadata.getBaseURI(), "RDF parser needs a base URI");
		Assert.notNull(metadata.getJobIdentifier(), "RDF parser needs a base URI");
		CustomRDFXMLParser parser = new CustomRDFXMLParser();
		parser.setRDFHandler(new CustomRDFHandler(conn, metadata.getJobIdentifier()));		
		parser.parse(in, metadata.getBaseURI());
    }
	
	private class CustomRDFXMLParser extends RDFXMLParser {
		@Override
		public synchronized void parse(InputStream in, String baseURI)
				throws IOException, RDFParseException, RDFHandlerException {
			logger.debug("USING BASE URI: "+baseURI);
			super.parse(in, baseURI);
//			StringWriter writer = new StringWriter();
//			IOUtils.copy(in, writer, "UTF-8");
//			logger.info(writer.toString());			
		}
		
		@Override
		protected Statement createStatement(Resource subject, URI predicate, Value object)
				throws RDFParseException {
			if (Literal.class.isAssignableFrom(object.getClass())) {
				Literal value = (Literal) object;
				if (value.getDatatype() != null 
						&& value.getDatatype().toString().equals("xsd:dateTime")) {
					object = vf.createLiteral(value.calendarValue());
				}		
			}
			return vf.createStatement(subject, predicate, object);
		}
	}
	
	private class CustomRDFHandler extends RDFHandlerBase {
		private RepositoryConnection conn;
		private Literal jobId;
		
		public CustomRDFHandler(RepositoryConnection conn, String identifier) {
			this.conn = conn;
			this.jobId = vf.createLiteral(identifier);
		}
		
		@Override
		public void handleStatement(Statement st) throws RDFHandlerException {
			try {
				Statement statement = getContextStatement(st);
//				logger.info(st.toString());
				conn.add(statement);
				conn.add(statement.getContext(), source, jobId);
			} catch (RepositoryException e) {
				throw new RDFHandlerException(e);
			}
		}
		
		private Statement getContextStatement(Statement st) {
			return vf.createStatement(st.getSubject(), st.getPredicate(), 
					st.getObject(), vf.createBNode());
		}
	}
}
