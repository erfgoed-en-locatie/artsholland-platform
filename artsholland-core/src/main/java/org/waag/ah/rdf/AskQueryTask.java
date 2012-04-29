package org.waag.ah.rdf;

import java.io.OutputStream;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;
import org.openrdf.repository.RepositoryConnection;
import org.waag.ah.QueryDefinition;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.WriterConfig;

public class AskQueryTask extends AbstractQueryTask {

	public AskQueryTask(RepositoryConnectionFactory cf, QueryDefinition query,
			WriterConfig config, OutputStream os) {
		super(cf, query, config, os);
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
				.getInstance().getFileFormatForMIMEType(config.getFormat());
		final BooleanQueryResultWriter w = BooleanQueryResultWriterRegistry
				.getInstance().get(format).getWriter(os);
		final boolean result = sailBooleanQuery.evaluate();
		w.write(result);
	}
}