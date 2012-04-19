package org.waag.ah.bigdata;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.FutureTask;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.AbstractQuery;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.QueryDefinition;
import org.waag.ah.QueryService;
import org.waag.ah.QueryTask;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.bigdata.BigdataQueryService.BigdataRDFContextWrapper.AbstractBigdataQueryTask;
import org.waag.ah.rdf.ConfigurableRDFWriter;
import org.waag.ah.rdf.RDFWriterConfig;

import com.bigdata.journal.IIndexManager;
import com.bigdata.journal.ITx;
import com.bigdata.rdf.sail.BigdataSailBooleanQuery;
import com.bigdata.rdf.sail.BigdataSailGraphQuery;
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection;
import com.bigdata.rdf.sail.BigdataSailTupleQuery;
import com.bigdata.rdf.sail.sparql.Bigdata2ASTSPARQLParser;
import com.bigdata.rdf.sail.webapp.SparqlEndpointConfig;
import com.bigdata.rdf.sparql.ast.ASTContainer;
import com.bigdata.rdf.sparql.ast.QueryType;

@Singleton
@DependsOn("BigdataConnectionService")
public class BigdataQueryService implements QueryService {
	private static final Logger logger = LoggerFactory
			.getLogger(BigdataQueryService.class);
	private BigdataRDFContextWrapper context;
	public static final String EXPLAIN = BigdataRDFContextWrapper.EXPLAIN;

	private static long BIGDATA_TIMESTAMP = ITx.READ_COMMITTED;
	private static String BIGDATA_NAMESPACE = "kb";
	
	@EJB(mappedName="java:module/BigdataConnectionService")
	private RepositoryConnectionFactory cf;

	@PostConstruct
	public void init() {
		context = new BigdataRDFContextWrapper(getConfig(), getIndexManager());
	}

	private IIndexManager getIndexManager() {
		return cf.getJournal();
	}

	final private SparqlEndpointConfig getConfig() {
		return new SparqlEndpointConfig(BIGDATA_NAMESPACE, BIGDATA_TIMESTAMP, 16);
	}
	
	public QueryTask getQueryTask(final QueryDefinition query,
			final RDFWriterConfig config, final OutputStream os)
			throws MalformedQueryException {

		AbstractBigdataQueryTask queryTask = context.getQueryTask(
				getConfig().namespace, getConfig().timestamp, query.getQuery(),
				config.getBaseUri(), config.getFormat(), os, config);
		return new QueryTaskWrapper(queryTask, query.getCountQuery());
	}
	
	public QueryTaskWrapper getQueryTask(final String query, final String baseURI,
			final String accept, final OutputStream os,
			final RDFWriterConfig config) throws MalformedQueryException {
		AbstractBigdataQueryTask queryTask = context.getQueryTask(
				getConfig().namespace, getConfig().timestamp, query, baseURI,
				accept, os, config);
		return new QueryTaskWrapper(queryTask, null);
	}

	public FutureTask<Void> executeQueryTask(QueryTask task) {
		FutureTask<Void> ft = new FutureTask<Void>(task);
		context.queryService.execute(ft);
		return ft;
	}

	public class QueryTaskWrapper implements QueryTask {
		private AbstractBigdataQueryTask queryTask;
		private String countQuery;

		public QueryTaskWrapper(AbstractBigdataQueryTask queryTask, String countQuery) {
			this.queryTask = queryTask;
			this.countQuery = countQuery;
		}

		public void setBinding(String key, Value value) {
			queryTask.setBinding(key, value);
		}

		@Override
		public Void call() throws Exception {
			return queryTask.call();
		}
		@Override
		public long getCount() throws UnsupportedOperationException {
			if (countQuery == null) {
				throw new UnsupportedOperationException(
						"No count qwuery specified");
			}
			BigdataSailRepositoryConnection conn = null;
			try {
				conn = context.getQueryConnection(BIGDATA_NAMESPACE, BIGDATA_TIMESTAMP);
				TupleQuery tupleQuery = conn.prepareTupleQuery(
						QueryLanguage.SPARQL, this.countQuery);
				TupleQueryResult result = tupleQuery.evaluate();
				if (result.hasNext()) {
					BindingSet next = result.next();
					if (next.hasBinding("count")) {
						Value value = next.getValue("count");
						if (value instanceof Literal) {
							return ((Literal) value).longValue();
						}
					}
				}
				return 0;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (RepositoryException e) {
						e.printStackTrace();
					}
				}
			}
			return 0;
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
	
	public static class BigdataRDFContextWrapper extends BigdataRDFContext {

		public BigdataRDFContextWrapper(SparqlEndpointConfig config,
				IIndexManager indexManager) {
			super(config, indexManager);
		}
	
		public AbstractBigdataQueryTask getQueryTask(String namespace,
				long timestamp, String queryStr, String baseURI, String acceptStr,
				OutputStream os, RDFWriterConfig config) throws MalformedQueryException {

			final ASTContainer astContainer = new Bigdata2ASTSPARQLParser(
					getTripleStore(namespace, timestamp)).parseQuery2(queryStr, baseURI);			
			if (logger.isDebugEnabled()) {
				logger.debug(astContainer.toString());
			}

			final QueryType queryType = astContainer.getOriginalAST().getQueryType();

			switch (queryType) {
				case ASK: {
					final BooleanQueryResultFormat format = BooleanQueryResultFormat
							.forMIMEType(acceptStr, BooleanQueryResultFormat.SPARQL);
					return new AskQueryTask(namespace, timestamp, baseURI, astContainer,
							queryType, format, os, config);
				}
				case DESCRIBE:
				case CONSTRUCT: {
					final RDFFormat format = RDFFormat.forMIMEType(acceptStr,
							RDFFormat.RDFXML);
					return new GraphQueryTask(namespace, timestamp, baseURI,
							astContainer, queryType, format, os, config);
				}
				case SELECT: {
					final TupleQueryResultFormat format = TupleQueryResultFormat
							.forMIMEType(acceptStr, TupleQueryResultFormat.SPARQL);
					return new TupleQueryTask(namespace, timestamp, baseURI,
							astContainer, queryType, format, os, config);
				}
			}

			throw new RuntimeException("Unknown query type: " + queryType);
		}

		public abstract class AbstractBigdataQueryTask extends AbstractQueryTask {
			private Map<String, Value> bindings = new HashMap<String, Value>();
			private RDFWriterConfig config;

			public AbstractBigdataQueryTask(String namespace, long timestamp,
					String baseURI, ASTContainer astContainer, QueryType queryType,
					String defaultMIMEType, Charset charset, String defaultFileExtension,
					OutputStream os, RDFWriterConfig config) {
				super(namespace, timestamp, baseURI, astContainer, queryType,
						defaultMIMEType, charset, defaultFileExtension,
						new MockHttpServletRequest(), os);
				this.config = config;
			}

			public void setBinding(String key, Value value) {
				bindings.put(key, value);
			}

			protected AbstractQuery buildQuery(
					final BigdataSailRepositoryConnection cxn) {
				AbstractQuery query = super.setupQuery(cxn);
				applyBindings(query);
				return query;
			}

			private void applyBindings(AbstractQuery query) {
				if (bindings.size() == 0) {
					return;
				}
				for (Entry<String, Value> binding : bindings.entrySet()) {
					query.setBinding(binding.getKey(), binding.getValue());
				}
			}
			
			protected void applyConfig(RDFWriter writer) {
				if (ConfigurableRDFWriter.class.isAssignableFrom(writer.getClass())) {
					((ConfigurableRDFWriter) writer).setConfig(config);
				}
			}
		}

		public class AskQueryTask extends AbstractBigdataQueryTask {

			public AskQueryTask(final String namespace, final long timestamp,
					final String baseURI, final ASTContainer astContainer,
					final QueryType queryType, final BooleanQueryResultFormat format,
					final OutputStream os, final RDFWriterConfig config) {
				super(namespace, timestamp, baseURI, astContainer, queryType, format
						.getDefaultMIMEType(), format.getCharset(), format
						.getDefaultFileExtension(), os, config);
			}

			protected void doQuery(final BigdataSailRepositoryConnection cxn,
					final OutputStream os) throws Exception {
				final BigdataSailBooleanQuery query = (BigdataSailBooleanQuery) buildQuery(cxn);
				final BooleanQueryResultFormat format = BooleanQueryResultWriterRegistry
						.getInstance().getFileFormatForMIMEType(mimeType);
				final BooleanQueryResultWriter w = BooleanQueryResultWriterRegistry
						.getInstance().get(format).getWriter(os);
				final boolean result = query.evaluate();
				w.write(result);
			}
		}

		public class GraphQueryTask extends AbstractBigdataQueryTask {

			public GraphQueryTask(final String namespace, final long timestamp,
					final String baseURI, final ASTContainer astContainer,
					final QueryType queryType, final RDFFormat format,
					final OutputStream os, final RDFWriterConfig config) {
				super(namespace, timestamp, baseURI, astContainer, queryType, format
						.getDefaultMIMEType(), format.getCharset(), format
						.getDefaultFileExtension(), os, config);
			}

			@Override
			protected void doQuery(final BigdataSailRepositoryConnection cxn,
					final OutputStream os) throws Exception {
				final BigdataSailGraphQuery query = (BigdataSailGraphQuery) buildQuery(cxn);
				final RDFFormat format = RDFWriterRegistry.getInstance()
						.getFileFormatForMIMEType(mimeType);
				final RDFWriter w = RDFWriterRegistry.getInstance().get(format)
						.getWriter(os);	
				applyConfig(w);
				query.evaluate(w);
			}
		}

		public class TupleQueryTask extends AbstractBigdataQueryTask {

			public TupleQueryTask(final String namespace, final long timestamp,
					final String baseURI, final ASTContainer astContainer,
					final QueryType queryType, final TupleQueryResultFormat format,
					final OutputStream os, final RDFWriterConfig config) {

				super(namespace, timestamp, baseURI, astContainer, queryType, format
						.getDefaultMIMEType(), format.getCharset(), format
						.getDefaultFileExtension(), os, config);
			}

			protected void doQuery(final BigdataSailRepositoryConnection cxn,
					final OutputStream os) throws Exception {
				final BigdataSailTupleQuery query = (BigdataSailTupleQuery) buildQuery(cxn);
				final TupleQueryResultFormat format = TupleQueryResultWriterRegistry
						.getInstance().getFileFormatForMIMEType(mimeType);
				final TupleQueryResultWriter w = TupleQueryResultWriterRegistry
						.getInstance().get(format).getWriter(os);
				query.evaluate(w);
			}
		}
	}
}
