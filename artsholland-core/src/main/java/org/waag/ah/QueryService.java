package org.waag.ah;

import java.io.OutputStream;

import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.parser.ParsedQuery;

import com.bigdata.rdf.sparql.ast.QueryType;

public interface QueryService {
	QueryTask getQueryTask(QueryDefinition query, WriterConfig config,
			OutputStream out) throws MalformedQueryException;

	ParsedQuery getParsedQuery(QueryDefinition query,
			WriterConfig config) throws MalformedQueryException;
	
	QueryTask getQueryTask(ParsedQuery parsedQuery, QueryDefinition query,
			WriterConfig config, OutputStream out)
			throws MalformedQueryException;

	QueryType getQueryType(ParsedQuery parsedQuery);

	GraphQueryResult executeQuery(String queryString)
			throws MalformedQueryException;

}
