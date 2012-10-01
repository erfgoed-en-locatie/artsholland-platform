package org.waag.ah.tinkerpop;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import com.tinkerpop.pipes.PipeFunction;

public class StatementKeyFunction implements PipeFunction<Statement, URI> {

	@Override
	public URI compute(Statement st) {
		return (URI) st.getSubject();
	}
}
