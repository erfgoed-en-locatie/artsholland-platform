package org.waag.ah.tinkerpop;

import java.util.List;
import java.util.NoSuchElementException;

import org.openrdf.model.Statement;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.rdf.GraphEnricher;
import org.waag.ah.rdf.NamedGraph;

import com.tinkerpop.pipes.AbstractPipe;

public abstract class AbstractEnricher extends
		AbstractPipe<NamedGraph, List<Statement>> implements GraphEnricher {
	private final EnricherConfig config;

	public AbstractEnricher(EnricherConfig config) {
		this.config = config;
	}

	@Override
	protected final List<Statement> processNextStart()
			throws NoSuchElementException {
		return enrich(config, this.starts.next());
	}
}
