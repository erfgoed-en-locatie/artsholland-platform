package org.waag.rdf.sesame;

import java.io.OutputStream;

import org.openrdf.query.GraphQuery;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.rdf.WriterConfig;

public class GraphQueryTask extends AbstractQueryTask {
	final static Logger logger = LoggerFactory.getLogger(GraphQueryTask.class);

	public GraphQueryTask(RepositoryConnection conn,
			QueryDefinition query, WriterConfig config, OutputStream os) {
		super(conn, query, config, os);
	}

	@Override
	protected void doQuery(QueryDefinition query, WriterConfig config,
			RepositoryConnection cxn, OutputStream os) throws Exception {
		final GraphQuery sailGraphQuery = cxn
				.prepareGraphQuery(query.getQueryLanguage(), query.getQuery(),
						config.getBaseUri());
		final RDFFormat format = RDFFormat.forMIMEType(config.getContentType(),
				RDFFormat.RDFXML);
		// final RDFFormat format = RDFWriterRegistry.getInstance()
		// .getFileFormatForMIMEType(config.getFormat());
		final RDFWriter w = RDFWriterRegistry.getInstance().get(format)
				.getWriter(os);
		applyConfig(w);
		sailGraphQuery.evaluate(w);
	}
}
