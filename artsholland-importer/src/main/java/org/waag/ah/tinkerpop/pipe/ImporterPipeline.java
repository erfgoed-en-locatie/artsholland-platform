package org.waag.ah.tinkerpop.pipe;

import java.net.URL;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.waag.ah.importer.ImporterConfig;

import com.tinkerpop.pipes.util.Pipeline;

public class ImporterPipeline extends Pipeline<URL, Statement> {

	public ImporterPipeline(ImporterConfig config, RepositoryConnection conn) {
		super();
		this.addPipe(new ParserPipeline());
		this.addPipe(new ProcessorPipeline());
		this.addPipe(new PersistDataPipe(config, conn));
	}
}
