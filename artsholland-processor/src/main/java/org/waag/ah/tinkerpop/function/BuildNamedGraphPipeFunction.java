package org.waag.ah.tinkerpop.function;

import java.util.List;
import java.util.Map.Entry;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.waag.ah.rdf.NamedGraph;

import com.tinkerpop.pipes.PipeFunction;

public class BuildNamedGraphPipeFunction implements
		PipeFunction<Entry<URI, List<Statement>>, NamedGraph> {

	@Override
	public NamedGraph compute(Entry<URI, List<Statement>> statements) {
//		return new AbstractMap.SimpleEntry<URI, Graph>(statements.getKey(),
//				new GraphImpl(statements.getValue()));
		return new NamedGraph(statements.getKey(), statements.getValue());
	}
}
