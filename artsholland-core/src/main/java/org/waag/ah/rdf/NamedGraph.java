package org.waag.ah.rdf;

import java.util.Collection;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.GraphImpl;

@SuppressWarnings("serial")
public class NamedGraph extends GraphImpl {
	private URI uri;

	public NamedGraph(URI uri) {
		super();
		this.uri = uri;
	}

	public NamedGraph(URI uri, Collection<? extends Statement> statements) {
		this(statements);
		this.uri = uri;
	}
	
	private NamedGraph(Collection<? extends Statement> statements) {
		super(statements);
	}
	
	public NamedGraph(URI uri, ValueFactory valueFactory,
			Collection<? extends Statement> statements) {
		this(valueFactory, statements);
		this.uri = uri;
	}
	
	private NamedGraph(ValueFactory valueFactory,
			Collection<? extends Statement> statements) {
		super(valueFactory, statements);
	}

	public NamedGraph(URI uri, ValueFactory valueFactory) {
		this(valueFactory);
		this.uri = uri;
	}
	
	private NamedGraph(ValueFactory valueFactory) {
		super(valueFactory);
	}

	public URI getGraphUri() {
		return this.uri;
	}
}
