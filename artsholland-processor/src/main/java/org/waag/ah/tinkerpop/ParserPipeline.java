package org.waag.ah.tinkerpop;

import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.model.Statement;

import com.tinkerpop.pipes.transform.ScatterPipe;
import com.tinkerpop.pipes.util.Pipeline;

public class ParserPipeline extends Pipeline<URL, Statement> {

	public ParserPipeline() throws ConfigurationException {
		super();
		this.addPipe(new TikaParserPipe());
		this.addPipe(new ObjectInputStreamReaderPipe());
		this.addPipe(new StatementGeneratorPipe());
		this.addPipe(new ScatterPipe<List<Statement>, Statement>());
	}
}
