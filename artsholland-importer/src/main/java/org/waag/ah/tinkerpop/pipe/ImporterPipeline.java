package org.waag.ah.tinkerpop.pipe;

import java.net.URL;

import org.openrdf.model.Statement;

import com.tinkerpop.pipes.util.Pipeline;

public class ImporterPipeline extends Pipeline<URL, Statement> {

	public ImporterPipeline() {
		super();
		this.addPipe(new ParserPipeline());
		this.addPipe(new ProcessorPipeline());
	}
}
