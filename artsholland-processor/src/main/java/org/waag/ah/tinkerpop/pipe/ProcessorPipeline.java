package org.waag.ah.tinkerpop.pipe;

import org.openrdf.model.Statement;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.tinkerpop.process.XsdDateTimeProcessor;

import com.tinkerpop.pipes.util.Pipeline;

public class ProcessorPipeline extends Pipeline<Statement, Statement> {
	
	public ProcessorPipeline() {
		this.addPipe(new XsdDateTimeProcessor(new EnricherConfig()));
	}
}
