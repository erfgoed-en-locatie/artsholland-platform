package org.waag.ah.rdf;

import java.io.OutputStream;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.sparql.SPARQLParserFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.waag.ah.QueryDefinition;
import org.waag.ah.QueryTask;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.WriterConfig;

import com.bigdata.rdf.sparql.ast.QueryType;

@Singleton
public class QueryService {
//	private static final Logger logger = LoggerFactory
//			.getLogger(QueryService.class);
	
	@EJB(mappedName="java:module/ConnectionService")
	private RepositoryConnectionFactory cf;

	public GraphQueryResult executeQuery(String query) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection conn = cf.getConnection();
		try {
			GraphQuery graphQuery = conn.prepareGraphQuery(QueryLanguage.SPARQL, query);
			return graphQuery.evaluate();
		} finally {
			conn.close();
		}
	}
	
	public QueryTask getQueryTask(QueryDefinition query,
			WriterConfig config, OutputStream out)
			throws MalformedQueryException, RepositoryException {
		QueryParser parser = new SPARQLParserFactory().getParser();
		ParsedQuery parsedQuery = parser.parseQuery(query.getQuery(),
				config.getBaseUri());
		RepositoryConnection conn = cf.getConnection();
		try {
			if (parsedQuery instanceof ParsedTupleQuery) {
				return new TupleQueryTask(conn, query, config, out);
			} else if (parsedQuery instanceof ParsedBooleanQuery) {
				return new AskQueryTask(conn, query, config, out);
			} else if (parsedQuery instanceof ParsedGraphQuery) {
				return new GraphQueryTask(conn, query, config, out);
			}			
		} finally {
//			conn.close();
		}
		throw new MalformedQueryException("Unknown query type: "
				+ ParsedQuery.class.getName());
	}
	
	public ParsedQuery getParsedQuery(QueryDefinition query,
			WriterConfig config) throws MalformedQueryException {
		QueryParser parser = new SPARQLParserFactory().getParser();
		return parser.parseQuery(query.getQuery(),
				config.getBaseUri());		
	}
	
	public QueryTask getQueryTask(ParsedQuery parsedQuery, QueryDefinition query,
			WriterConfig config, OutputStream out)
			throws MalformedQueryException, RepositoryException {
		RepositoryConnection conn = cf.getConnection();
		try {
			if (parsedQuery instanceof ParsedTupleQuery) {
				return new TupleQueryTask(conn, query, config, out);
			} else if (parsedQuery instanceof ParsedBooleanQuery) {
				return new AskQueryTask(conn, query, config, out);
			} else if (parsedQuery instanceof ParsedGraphQuery) {
				return new GraphQueryTask(conn, query, config, out);
			}			
		} finally {
//			conn.close();
		}
		throw new MalformedQueryException("Unknown query type: "
				+ ParsedQuery.class.getName());
	}

	public QueryType getQueryType(ParsedQuery parsedQuery) {
		if (parsedQuery instanceof ParsedTupleQuery) {
			return QueryType.SELECT;
		} else if (parsedQuery instanceof ParsedBooleanQuery) {
			return QueryType.ASK;
		} else if (parsedQuery instanceof ParsedGraphQuery) {
			return QueryType.CONSTRUCT;
		}	else {
			return null;
		}
	}
}
