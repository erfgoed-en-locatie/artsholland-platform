package org.waag.ah.tinkerpop;

import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.model.Statement;
import org.waag.ah.importer.ImportConfig;

import com.tinkerpop.pipes.transform.ScatterPipe;
import com.tinkerpop.pipes.util.Pipeline;

public class DocumentParserPipeline extends Pipeline<URL, Statement> {

	public DocumentParserPipeline() throws ConfigurationException {
		super();
		this.addPipe(new StreamingTikaParserPipe());
		this.addPipe(new ObjectInputStreamReaderPipe());
		this.addPipe(new StatementGeneratorPipe());
		this.addPipe(new ScatterPipe<List<Statement>, Statement>());
	}

	public DocumentParserPipeline(ImportConfig config) {
		// TODO Auto-generated constructor stub
	}
}
