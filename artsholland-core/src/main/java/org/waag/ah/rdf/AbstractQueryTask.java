package org.waag.ah.rdf;

import java.io.OutputStream;
import java.io.PrintStream;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.QueryDefinition;
import org.waag.ah.QueryTask;
import org.waag.ah.WriterConfig;

public abstract class AbstractQueryTask implements QueryTask {
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractQueryTask.class);
	
	private RepositoryConnection conn;
	private OutputStream os;
	private final QueryDefinition query;
	private final WriterConfig config;

	public AbstractQueryTask(RepositoryConnection conn, QueryDefinition query,
			WriterConfig config, OutputStream os) {
		this.conn = conn;
		this.query = query;
		this.config = config;
		this.os = os;
	}
	
	@Override
	public long getCount() throws UnsupportedOperationException,
			MalformedQueryException, RepositoryException, QueryEvaluationException {
		String countQuery = query.getCountQuery();
		if (countQuery == null) {
			throw new UnsupportedOperationException("No count query specified");
		}
		try {
			TupleQuery tupleQuery = conn.prepareTupleQuery(
					QueryLanguage.SPARQL, countQuery);
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
		} catch (MalformedQueryException e) {
			throw e;
//		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}
//		return 0;
	}

	@Override
	public Void call() throws Exception{
		try {
			String jsonpCallback = null;
			PrintStream printStream = null;
			
			if (config.isJSONP()) {
				jsonpCallback = config.getJSONPCallback();				
				
				printStream = new PrintStream(os);
				printStream.print(jsonpCallback + "(");
			}
			
			doQuery(query, config, conn, os);
			
			if (config.isJSONP()) {
				printStream.print(");");	
			}			
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {}
		}
		return null;
	}

	protected void applyConfig(RDFWriter writer) {
		if (ConfigurableRDFWriter.class.isAssignableFrom(writer.getClass())) {
			((ConfigurableRDFWriter) writer).setConfig(config);
		}
	}

	protected abstract void doQuery(QueryDefinition query, WriterConfig config,
			RepositoryConnection cxn, OutputStream os) throws Exception;

	// protected BigdataSailQuery buildQuery() {
	// AbstractQuery query = final AbstractQuery query = newQuery(cxn);
	// applyBindings(query);
	// return newQuery(cxn);
	// }
	// private SailQuery newQuery(SailConnection cxn) {

	// final ASTContainer astContainer = ((BigdataParsedQuery) parsedQuery)
	// .getASTContainer();

	// final QueryType queryType = ((BigdataParsedQuery) parsedQuery)
	// .getQueryType();

	// switch (queryType) {
	// case SELECT:
	// return new BigdataSailTupleQuery(astContainer, cxn);
	// case DESCRIBE:
	// case CONSTRUCT:
	// // return new BigdataSailGraphQuery(astContainer, cxn);
	// TupleQuery tupleQuery = cxn.p
	// return new SailGraphQuery(cxn);
	// case ASK: {
	// return new BigdataSailBooleanQuery(astContainer, cxn);
	// }
	// default:
	// throw new RuntimeException("Unknown query type: " + queryType);
	// }
	// }

	// public AbstractBigdataQueryTask(String namespace, long timestamp,
	// String baseURI, ASTContainer astContainer, QueryType queryType,
	// String defaultMIMEType, Charset charset, String defaultFileExtension,
	// OutputStream os, RDFWriterConfig config) {
	// super(namespace, timestamp, baseURI, astContainer, queryType,
	// defaultMIMEType, charset, defaultFileExtension,
	// new MockHttpServletRequest(), os);
	// this.config = config;
	// }

	// public AbstractQueryTask(final String mimeType) {
	// this.mimeType = mimeType;
	// }
	//
	// abstract protected void doQuery(BigdataSailRepositoryConnection cxn,
	// OutputStream os) throws Exception;
	//
	// public void setBinding(String key, Value value) {
	// bindings.put(key, value);
	// }
	//
	// protected AbstractQuery buildQuery(
	// final BigdataSailRepositoryConnection cxn) {
	// AbstractQuery query = setupQuery(cxn);
	// applyBindings(query);
	// return query;
	// }
	//
	// final AbstractQuery setupQuery(final BigdataSailRepositoryConnection cxn)
	// {
	//
	// // Note the begin time for the query.
	// final long begin = System.nanoTime();
	//
	// final AbstractQuery query = newQuery(cxn);
	//
	// // Figure out the UUID under which the query will execute.
	// final UUID queryId2 = setQueryId(((BigdataSailQuery) query)
	// .getASTContainer());
	//
	// // Override query if data set protocol parameters were used.
	// overrideDataset(query);
	//
	// if (analytic != null) {
	//
	// // Turn analytic query on/off as requested.
	// // astContainer.getOriginalAST().setQueryHint(QueryHints.ANALYTIC,
	// // analytic.toString());
	// astContainer.setQueryHint(QueryHints.ANALYTIC,
	// analytic.toString());
	//
	// }
	//
	// // Set the query object.
	// this.sailQueryOrUpdate = query;
	//
	// // Set the IRunningQuery's UUID (volatile write!)
	// this.queryId2 = queryId2;
	//
	// // Stuff it in the map of running queries.
	// m_queries.put(queryId, new RunningQuery(queryId.longValue(),
	// queryId2, begin, this));
	//
	// return query;
	//
	// }
	//
	// private void applyBindings(AbstractQuery query) {
	// if (bindings.size() == 0) {
	// return;
	// }
	// for (Entry<String, Value> binding : bindings.entrySet()) {
	// query.setBinding(binding.getKey(), binding.getValue());
	// }
	// }
	//
	// protected void applyConfig(RDFWriter writer) {
	// if (ConfigurableRDFWriter.class.isAssignableFrom(writer.getClass())) {
	// ((ConfigurableRDFWriter) writer).setConfig(config);
	// }
	// }
}