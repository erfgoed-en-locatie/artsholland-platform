package org.waag.ah;

import java.io.OutputStream;
import java.util.concurrent.FutureTask;

import org.openrdf.query.MalformedQueryException;
import org.waag.ah.rdf.RDFWriterConfig;

public interface QueryService {
	QueryTask getQueryTask(QueryDefinition query, RDFWriterConfig config,
			OutputStream out) throws MalformedQueryException;
	FutureTask<Void> executeQueryTask(QueryTask task);
//	Object doQuery(String countQuery);
}
