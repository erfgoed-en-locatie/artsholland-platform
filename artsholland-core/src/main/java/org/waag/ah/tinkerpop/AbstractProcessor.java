package org.waag.ah.tinkerpop;

import java.util.NoSuchElementException;

import org.openrdf.model.Statement;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.rdf.StatementProcessor;

import com.tinkerpop.pipes.AbstractPipe;

public abstract class AbstractProcessor extends
		AbstractPipe<Statement, Statement> implements StatementProcessor {
	private final EnricherConfig config;

	public AbstractProcessor(EnricherConfig config) {
		this.config = config;
	}

	@Override
	protected final Statement processNextStart()
			throws NoSuchElementException {
		return process(config, this.starts.next());
	}
}

