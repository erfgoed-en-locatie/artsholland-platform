package org.waag.ah.tinkerpop;

import java.util.List;

import org.openrdf.model.Statement;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.rdf.NamedGraph;
import org.waag.ah.tinkerpop.function.GraphFilterPipeFunction;

import com.tinkerpop.pipes.filter.FilterFunctionPipe;
import com.tinkerpop.pipes.util.Pipeline;

public class EnricherFactory {

	public static Pipeline<NamedGraph, List<Statement>> getInstance(
			EnricherConfig config) {
		Pipeline<NamedGraph, List<Statement>> pipeline = new Pipeline<NamedGraph, List<Statement>>();

		// Add object filter.
		pipeline.addPipe(new FilterFunctionPipe<NamedGraph>(
				new GraphFilterPipeFunction(config.getObjectUri(), config
						.getIncludeUris())));

		// Add enricher function.
		try {
			AbstractEnricher enricher = (AbstractEnricher) config.getEnricher()
					.getConstructor(EnricherConfig.class).newInstance(config);
			pipeline.addPipe(enricher);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return pipeline;
	}
}
