package org.waag.ah.tinkerpop.function;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.tinkerpop.pipes.PipeFunction;

public class URIFilterPipeFunction implements PipeFunction<Statement, Boolean> {
	private URI object;
	private URI predicate;

	public URIFilterPipeFunction(URI object) {
		this.object = object;
		this.predicate = ValueFactoryImpl.getInstance().createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
	}

	@Override
	public Boolean compute(Statement st) {
		return st.getPredicate().equals(predicate) && st.getObject().equals(object);
	}
}
