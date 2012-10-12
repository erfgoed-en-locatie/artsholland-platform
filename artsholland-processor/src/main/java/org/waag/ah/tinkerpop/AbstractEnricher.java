package org.waag.ah.tinkerpop;

import java.util.List;
import java.util.NoSuchElementException;

import org.openrdf.model.Statement;
import org.waag.rdf.sesame.EnricherConfig;
import org.waag.rdf.sesame.GraphEnricher;
import org.waag.rdf.sesame.NamedGraph;

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
