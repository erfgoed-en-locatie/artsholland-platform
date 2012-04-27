package org.waag.ah.bigdata;

import java.io.OutputStream;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.openrdf.query.MalformedQueryException;
import org.waag.ah.QueryDefinition;
import org.waag.ah.QueryService;
import org.waag.ah.QueryTask;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.WriterConfig;
import org.waag.ah.rdf.AskQueryTask;
import org.waag.ah.rdf.GraphQueryTask;
import org.waag.ah.rdf.TupleQueryTask;

import com.bigdata.journal.ITx;
import com.bigdata.journal.TimestampUtility;
import com.bigdata.rdf.sail.sparql.Bigdata2ASTSPARQLParser;
import com.bigdata.rdf.sparql.ast.ASTContainer;
import com.bigdata.rdf.sparql.ast.QueryType;
import com.bigdata.rdf.store.AbstractTripleStore;

@Singleton
@DependsOn("BigdataConnectionService")
public class BigdataQueryService implements QueryService {
//	private static final Logger logger = LoggerFactory
//			.getLogger(BigdataQueryService.class);
//	public static final String EXPLAIN = BigdataRDFContext.EXPLAIN;
	private static long BIGDATA_TIMESTAMP = ITx.READ_COMMITTED;
	private static String BIGDATA_NAMESPACE = "kb";
	
	@EJB(mappedName="java:module/BigdataConnectionService")
	private RepositoryConnectionFactory cf;
	
//	private PlatformConfig config;

//	@PostConstruct
//	public void init() throws ConfigurationException {
//		this.config = PlatformConfigHelper.getConfig();
//		this.baseUri = config.getString("platform.baseUri"); 
//	}

	@Override
	public QueryTask getQueryTask(QueryDefinition query,
			WriterConfig config, OutputStream out)
			throws MalformedQueryException {
		QueryType queryType = getQueryType(query.getQuery(),
				config.getBaseUri());
		switch (queryType) {
			case ASK: {
				return new AskQueryTask(cf, query, config, out);
			}
			case DESCRIBE:
			case CONSTRUCT: {
				return new GraphQueryTask(cf, query, config, out);
			}
			case SELECT: {
				return new TupleQueryTask(cf, query, config, out);
			}
		}
		throw new RuntimeException("Unknown query type: " + queryType);		
	}
	
	private final QueryType getQueryType(String query, String baseUri) throws MalformedQueryException {
		final ASTContainer astContainer = new Bigdata2ASTSPARQLParser(
				getTripleStore(BigdataQueryService.BIGDATA_NAMESPACE,
						BigdataQueryService.BIGDATA_TIMESTAMP)).parseQuery2(
				query, baseUri);
		return astContainer.getOriginalAST().getQueryType();		
	}
	
    private AbstractTripleStore getTripleStore(final String namespace,
            final long timestamp) {
        final AbstractTripleStore tripleStore = (AbstractTripleStore) cf.getJournal()
                .getResourceLocator().locate(namespace, timestamp);
        if (tripleStore == null) {
            throw new RuntimeException("Not found: namespace=" + namespace
                    + ", timestamp=" + TimestampUtility.toString(timestamp));
        }
        return tripleStore;
    }

////	final private SparqlEndpointConfig getConfig() {
////		return new SparqlEndpointConfig(BIGDATA_NAMESPACE, BIGDATA_TIMESTAMP, 16);
////	}
//	
//	public QueryTask getQueryTask(final QueryDefinition query,
//			final RDFWriterConfig config, final OutputStream os)
//			throws MalformedQueryException {
//
//		AbstractQueryTask queryTask = context.getQueryTask(query.getQuery(),
//				config.getBaseUri(), config.getFormat(), os, config);
//		return new BigdataQueryTask(queryTask, query.getCountQuery());
//	}
//	
//	public BigdataQueryTask getQueryTask(final String query, final String baseURI,
//			final String accept, final OutputStream os,
//			final RDFWriterConfig config) throws MalformedQueryException {
//		AbstractQueryTask queryTask = context.getQueryTask(query, baseURI,
//				accept, os, config);
//		return new BigdataQueryTask(queryTask, null);
//	}
//
//	public FutureTask<Void> executeQueryTask(QueryTask task) {
//		FutureTask<Void> ft = new FutureTask<Void>(task);
////		context.queryService.execute(ft);
//		ft.run();
//		return ft;
//	}
//
//	public class BigdataQueryTask implements QueryTask {
//		private AbstractQueryTask queryTask;
//		private String countQuery;
//
//		public BigdataQueryTask(AbstractQueryTask queryTask, String countQuery) {
//			this.queryTask = queryTask;
//			this.countQuery = countQuery;
//		}
//
////		public void setBinding(String key, Value value) {
////			queryTask.setBinding(key, value);
////		}
//
//		@Override
//		public Void call() throws Exception {
//			return queryTask.call();
//		}
//		@Override
//		public long getCount() throws UnsupportedOperationException {
//			if (countQuery == null) {
//				throw new UnsupportedOperationException(
//						"No count qwuery specified");
//			}
//			SailConnection conn = null;
//			try {
////				conn = context.getQueryConnection();
////				TupleQuery tupleQuery = conn.prepareTupleQuery(
////						QueryLanguage.SPARQL, this.countQuery);
////				TupleQueryResult result = tupleQuery.evaluate();
//				if (result.hasNext()) {
//					BindingSet next = result.next();
//					if (next.hasBinding("count")) {
//						Value value = next.getValue("count");
//						if (value instanceof Literal) {
//							return ((Literal) value).longValue();
//						}
//					}
//				}
//				return 0;
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (conn != null) {
//					try {
//						conn.close();
//					} catch (RepositoryException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			return 0;
//		}
//
////		public boolean getExplain() {
////			return queryTask.explain;
////		}
////
////		public Charset getCharset() {
////			return queryTask.charset;
////		}
////
////		public String getMimeType() {
////			return queryTask.mimeType;
////		}
////
////		public long getTimestamp() {
////			return queryTask.timestamp;
////		}
////
////		public long getQueryId() {
////			return queryTask.queryId;
////		}
////
////		public String getFileExt() {
////			return queryTask.fileExt;
////		}
//	}
//	
//	private class BigdataRDFContext {
//		private Sail sail;
//		
//	    public BigdataRDFContext(Sail sail2) {
//	    	this.sail = sail;
//		}
//
//		public SailConnection getQueryConnection() throws SailException {
//	        return sail.getConnection();
//	    }
//	    
//        final public Void call() throws Exception {
//			SailConnection cxn = null;
//            try {
//                cxn = getQueryConnection();
////    			if(explain) {
////    				doQuery(cxn, new NullOutputStream());
////    			} else {
//				doQuery(cxn, os);
//                os.flush();
//                os.close();
////    			}
//                return null;
//            } finally {
//                if (cxn != null) {
//                    try {
//                        cxn.close();
//                    } catch (Throwable t) {
//                        logger.error(t.getMessage(), t);
//                    }
//                }
//            }
//        } // call()
//	
//		public AbstractQueryTask getQueryTask(String queryStr,
//				String baseURI, String acceptStr, OutputStream os,
//				RDFWriterConfig config) throws MalformedQueryException {
//
//			final ASTContainer astContainer = new Bigdata2ASTSPARQLParser(
//					getTripleStore(BigdataQueryService.BIGDATA_NAMESPACE,
//							BigdataQueryService.BIGDATA_TIMESTAMP))
//					.parseQuery2(queryStr, baseURI);
//			final QueryType queryType = astContainer.getOriginalAST().getQueryType();
//
//			switch (queryType) {
//				case ASK: {
//					final BooleanQueryResultFormat format = BooleanQueryResultFormat
//							.forMIMEType(acceptStr, BooleanQueryResultFormat.SPARQL);
//					return new AskQueryTask(baseURI, queryType, format, os, config);
//				}
//				case DESCRIBE:
//				case CONSTRUCT: {
//					final RDFFormat format = RDFFormat.forMIMEType(acceptStr,
//							RDFFormat.RDFXML);
//					return new GraphQueryTask(baseURI, queryType, format, os,
//							config);
//				}
//				case SELECT: {
//					final TupleQueryResultFormat format = TupleQueryResultFormat
//							.forMIMEType(acceptStr, TupleQueryResultFormat.SPARQL);
//					return new TupleQueryTask(baseURI, queryType, format, os,
//							config);
//				}
//			}
//
//			throw new RuntimeException("Unknown query type: " + queryType);
//		}
//		
//	    public AbstractTripleStore getTripleStore(final String namespace,
//	            final long timestamp) {
//	        
////	        if (timestamp == ITx.UNISOLATED)
////	            throw new IllegalArgumentException("UNISOLATED reads disallowed.");
//
//	        // resolve the default namespace.
//	        final AbstractTripleStore tripleStore = (AbstractTripleStore) getIndexManager()
//	                .getResourceLocator().locate(namespace, timestamp);
//
//	        if (tripleStore == null) {
//
//	            throw new RuntimeException("Not found: namespace=" + namespace
//	                    + ", timestamp=" + TimestampUtility.toString(timestamp));
//
//	        }
//
//	        return tripleStore;
//	        
//	    }
//	}
}
