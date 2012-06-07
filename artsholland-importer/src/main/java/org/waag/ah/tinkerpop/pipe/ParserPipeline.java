package org.waag.ah.tinkerpop.pipe;

import java.net.URL;
import java.util.List;

import org.openrdf.model.Statement;

import com.tinkerpop.pipes.transform.ScatterPipe;
import com.tinkerpop.pipes.util.Pipeline;

public class ParserPipeline extends Pipeline<URL, Statement> {

	public ParserPipeline() {
		super();
		this.addPipe(new TikaParserPipe());
		this.addPipe(new StatementGeneratorPipe());
		this.addPipe(new ScatterPipe<List<Statement>, Statement>());
	}
}
