package org.waag.ah.rdf;

import java.io.OutputStream;

import org.openrdf.query.TupleQuery;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.repository.RepositoryConnection;
import org.waag.ah.QueryDefinition;
import org.waag.ah.WriterConfig;

public class TupleQueryTask extends AbstractQueryTask {

	public TupleQueryTask(RepositoryConnection conn, QueryDefinition query,
			WriterConfig config, OutputStream os) {
		super(conn, query, config, os);
	}

	@Override
	protected void doQuery(QueryDefinition query, WriterConfig config,
			RepositoryConnection cxn, OutputStream os) throws Exception {
		final TupleQuery sailTupleQuery = cxn
				.prepareTupleQuery(query.getQueryLanguage(), query.getQuery(),
						config.getBaseUri());
		final TupleQueryResultFormat format = TupleQueryResultWriterRegistry
				.getInstance().getFileFormatForMIMEType(config.getFormat());
		final TupleQueryResultWriter w = TupleQueryResultWriterRegistry
				.getInstance().get(format).getWriter(os);
		sailTupleQuery.evaluate(w);
	}
}
