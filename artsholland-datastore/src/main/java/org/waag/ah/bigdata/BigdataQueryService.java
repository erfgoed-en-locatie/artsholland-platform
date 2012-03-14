package org.waag.ah.bigdata;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.ejb.Singleton;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.bigdata.BigdataRDFContext.AbstractQueryTask;

import com.bigdata.journal.IIndexManager;
import com.bigdata.journal.ITx;
import com.bigdata.journal.Journal;
import com.bigdata.journal.Options;
import com.bigdata.rdf.sail.BigdataSailBooleanQuery;
import com.bigdata.rdf.sail.BigdataSailGraphQuery;
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection;
import com.bigdata.rdf.sail.BigdataSailTupleQuery;
import com.bigdata.rdf.sail.sparql.Bigdata2ASTSPARQLParser;
import com.bigdata.rdf.sail.webapp.SparqlEndpointConfig;
import com.bigdata.rdf.sparql.ast.ASTContainer;
import com.bigdata.rdf.sparql.ast.QueryType;

@Singleton
public class BigdataQueryService {
	private static final Logger logger = LoggerFactory.getLogger(BigdataQueryService.class);
	private final BigdataRDFContextWrapper context;
	public static final String EXPLAIN = BigdataRDFContextWrapper.EXPLAIN;
	
	public BigdataQueryService() throws ConfigurationException {
		context = new BigdataRDFContextWrapper(getConfig(), getIndexManager());
	}
	
	private IIndexManager getIndexManager() throws ConfigurationException {
		PropertiesConfiguration config = PlatformConfig.getConfig(); 
		PropertiesConfiguration properties = 
				new PropertiesConfiguration("bigdata.properties");
		properties.setProperty(Options.FILE, config.getProperty("bigdata.journal"));
        return new Journal(ConfigurationConverter.getProperties(properties));
	}
    
    final private SparqlEndpointConfig getConfig() {
        return new SparqlEndpointConfig("kb", ITx.READ_COMMITTED, 16);
    } 
    
    public QueryTask getQueryTask(
            final String query, final String baseURI, final String accept, 
            final OutputStream os) throws MalformedQueryException {
        
		AbstractQueryTask queryTask = context.getQueryTask(
				getConfig().namespace, getConfig().timestamp, 
				query, baseURI, accept, os);
		
		return new QueryTask(queryTask);
    }

	public void executeQueryTask(FutureTask<Void> task) {
		context.queryService.execute(task);
	}
	
	public static class QueryTask implements Callable<Void> {
		private AbstractQueryTask queryTask;

		public QueryTask(AbstractQueryTask queryTask) {
			this.queryTask = queryTask;
		}

		@Override
		public Void call() throws Exception {
			return queryTask.call();
		}
		
		public boolean getExplain() {
			return queryTask.explain;
		}

		public Charset getCharset() {
			return queryTask.charset;
		}  
		
		public String getMimeType() {
			return queryTask.mimeType;
		}

		public long getTimestamp() {
			return queryTask.timestamp;
		}

		public long getQueryId() {
			return queryTask.queryId;
		}

		public String getFileExt() {
			return queryTask.fileExt;
		}
	}
	
	private static class BigdataRDFContextWrapper extends BigdataRDFContext {

		public BigdataRDFContextWrapper(SparqlEndpointConfig config,
				IIndexManager indexManager) {
			super(config, indexManager);
		}
		
		private abstract class AbstractQueryTaskWrapper extends AbstractQueryTask {

			public AbstractQueryTaskWrapper(String namespace, long timestamp,
					String baseURI, ASTContainer astContainer,
					QueryType queryType, String defaultMIMEType,
					Charset charset, String defaultFileExtension,
					OutputStream os) {
				super(namespace, timestamp, baseURI, astContainer, queryType, defaultMIMEType,
						charset, defaultFileExtension, new MockHttpServletRequest(), os);
			}
		}

		public AbstractQueryTask getQueryTask(String namespace, long timestamp,
				String queryStr, String baseURI, String acceptStr, OutputStream os) 
				throws MalformedQueryException {

	        final ASTContainer astContainer = new Bigdata2ASTSPARQLParser(
	                getTripleStore(namespace, timestamp)).parseQuery2(queryStr,
	                baseURI);

	        if (logger.isDebugEnabled()) {
	            logger.debug(astContainer.toString());
	        }

	        final QueryType queryType = astContainer.getOriginalAST()
	                .getQueryType();

	        switch (queryType) {
		        case ASK: {
		            final BooleanQueryResultFormat format = BooleanQueryResultFormat
		                    .forMIMEType(acceptStr, BooleanQueryResultFormat.SPARQL);
		            return new AskQueryTask(namespace, timestamp, baseURI,
		                    astContainer, queryType, format, os);
		        }
	        	case DESCRIBE:	
		        case CONSTRUCT: {
		            final RDFFormat format = RDFFormat.forMIMEType(acceptStr,
		                    RDFFormat.RDFXML);
		            return new GraphQueryTask(namespace, timestamp, baseURI,
		                    astContainer, queryType, format, os);
		        }
		        case SELECT: {
		            final TupleQueryResultFormat format = TupleQueryResultFormat
		                    .forMIMEType(acceptStr, TupleQueryResultFormat.SPARQL);
		            return new TupleQueryTask(namespace, timestamp, baseURI,
		                    astContainer, queryType, format, os);
		        }
	        }

	        throw new RuntimeException("Unknown query type: " + queryType);
		}
		
	    private class AskQueryTask extends AbstractQueryTaskWrapper {

	        public AskQueryTask(final String namespace, final long timestamp,
	                final String baseURI,
	                final ASTContainer astContainer, final QueryType queryType,
	                final BooleanQueryResultFormat format, final OutputStream os) {
	            super(namespace, timestamp, baseURI, astContainer,
	                    queryType, format.getDefaultMIMEType(),
	                    format.getCharset(), format.getDefaultFileExtension(), os);
	        }

	        protected void doQuery(final BigdataSailRepositoryConnection cxn,
	                final OutputStream os) throws Exception {
	            final BigdataSailBooleanQuery query = (BigdataSailBooleanQuery) setupQuery(cxn);
	            final BooleanQueryResultFormat format = BooleanQueryResultWriterRegistry
	                    .getInstance().getFileFormatForMIMEType(mimeType);
	            final BooleanQueryResultWriter w = BooleanQueryResultWriterRegistry
	                    .getInstance().get(format).getWriter(os);
	            final boolean result = query.evaluate();
	            w.write(result);
	        }
	    }
	    
	    private class GraphQueryTask extends AbstractQueryTaskWrapper {

	        public GraphQueryTask(final String namespace, final long timestamp,
	                final String baseURI,
	                final ASTContainer astContainer, final QueryType queryType,
	                final RDFFormat format, final OutputStream os) {
	            super(namespace, timestamp, baseURI, astContainer,
	                    queryType, format.getDefaultMIMEType(),
	                    format.getCharset(), format.getDefaultFileExtension(),
	                    os);
	        }

			@Override
			protected void doQuery(final BigdataSailRepositoryConnection cxn,
					final OutputStream os) throws Exception {
	            final BigdataSailGraphQuery query = (BigdataSailGraphQuery) setupQuery(cxn);
	            final RDFFormat format = RDFWriterRegistry.getInstance()
	                    .getFileFormatForMIMEType(mimeType);
	            final RDFWriter w = RDFWriterRegistry.getInstance().get(format)
	                    .getWriter(os);
				query.evaluate(w);
	        }
		}
	    
		private class TupleQueryTask extends AbstractQueryTaskWrapper {

	        public TupleQueryTask(final String namespace, final long timestamp,
	                final String baseURI,
	                final ASTContainer astContainer, final QueryType queryType,
	                final TupleQueryResultFormat format,
	                final OutputStream os) {

	            super(namespace, timestamp, baseURI, astContainer,
	                    queryType, format.getDefaultMIMEType(),
	                    format.getCharset(), format.getDefaultFileExtension(), os);
			}

			protected void doQuery(final BigdataSailRepositoryConnection cxn,
					final OutputStream os) throws Exception {
	            final BigdataSailTupleQuery query = (BigdataSailTupleQuery) setupQuery(cxn);
	            final TupleQueryResultFormat format = TupleQueryResultWriterRegistry
	                    .getInstance().getFileFormatForMIMEType(mimeType);
	            final TupleQueryResultWriter w = TupleQueryResultWriterRegistry
	                    .getInstance().get(format).getWriter(os);
				query.evaluate(w);
			}
		}
	}
}
