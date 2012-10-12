package org.waag.rdf.sesame;

import java.io.OutputStream;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;
import org.openrdf.repository.RepositoryConnection;
import org.waag.rdf.WriterConfig;

public class AskQueryTask extends AbstractQueryTask {

	public AskQueryTask(RepositoryConnection conn, QueryDefinition query,
			WriterConfig config, OutputStream os) {
		super(conn, query, config, os);
	}

	@Override
	protected void doQuery(QueryDefinition query, WriterConfig config,
			RepositoryConnection cxn, OutputStream os) throws Exception {
		final BooleanQuery sailBooleanQuery = cxn
				.prepareBooleanQuery(query.getQueryLanguage(),
						query.getQuery(), config.getBaseUri());
//		final BooleanQueryResultFormat format = BooleanQueryResultFormat
//		.forMIMEType(config.getFormat(), BooleanQueryResultFormat.SPARQL);
		final BooleanQueryResultFormat format = BooleanQueryResultWriterRegistry
				.getInstance().getFileFormatForMIMEType(config.getContentType());
		final BooleanQueryResultWriter w = BooleanQueryResultWriterRegistry
				.getInstance().get(format).getWriter(os);
		final boolean result = sailBooleanQuery.evaluate();
		w.write(result);
	}
}
