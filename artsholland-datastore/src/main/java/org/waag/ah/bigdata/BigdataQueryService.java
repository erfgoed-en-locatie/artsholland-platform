package org.waag.ah.bigdata;

import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.sparql.SPARQLParserFactory;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.QueryDefinition;
import org.waag.ah.QueryService;
import org.waag.ah.QueryTask;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.WriterConfig;
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.rdf.AskQueryTask;
import org.waag.ah.rdf.GraphQueryTask;
import org.waag.ah.rdf.TupleQueryTask;

@Singleton
@DependsOn("BigdataConnectionService")
public class BigdataQueryService implements QueryService {
	private static final Logger logger = LoggerFactory
			.getLogger(BigdataQueryService.class);
	
	@EJB(mappedName="java:module/BigdataConnectionService")
	private RepositoryConnectionFactory cf;

	private RepositoryConnection conn;

	@PostConstruct
	public void connect() {
		try {
			this.conn = cf.getConnection();
		} catch (ConnectionException e) {
			logger.error(e.getMessage());
		}
	}
	
	@Override
	public QueryTask getQueryTask(QueryDefinition query,
			WriterConfig config, OutputStream out)
			throws MalformedQueryException {

		QueryParser parser = new SPARQLParserFactory().getParser();
		ParsedQuery parsedQuery = parser.parseQuery(query.getQuery(),
				config.getBaseUri());
		try {
			if (parsedQuery instanceof ParsedTupleQuery) {
				return new TupleQueryTask(conn, query, config, out);
			} else if (parsedQuery instanceof ParsedBooleanQuery) {
				return new AskQueryTask(conn, query, config, out);
			} else if (parsedQuery instanceof ParsedGraphQuery) {
				return new GraphQueryTask(conn, query, config, out);
			}			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
		throw new MalformedQueryException("Unknown query type: "
				+ ParsedQuery.class.getName());
	}
	
	@Override
	public GraphQueryResult executeQuery(String queryString)
			throws MalformedQueryException {
		try {
			GraphQuery query = conn.prepareGraphQuery(QueryLanguage.SPARQL,
					queryString);
			return query.evaluate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
