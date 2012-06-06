package org.waag.ah;

import java.io.OutputStream;

import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;

public interface QueryService {
	QueryTask getQueryTask(QueryDefinition query, WriterConfig config,
			OutputStream out) throws MalformedQueryException;

	GraphQueryResult executeQuery(String queryString)
			throws MalformedQueryException;
}
