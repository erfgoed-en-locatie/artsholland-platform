package org.waag.ah.tinkerpop.function;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.waag.ah.rdf.NamedGraph;

import com.tinkerpop.pipes.PipeFunction;

public class GraphFilterPipeFunction implements
		PipeFunction<NamedGraph, Boolean> {
	private ValueFactoryImpl vf;
	
	private URI object;
	private URI predicate;
	private Set<URI> properties = new HashSet<URI>();
	
	public GraphFilterPipeFunction(URI objectUri) {
		this(objectUri, null);
	}
	
	public GraphFilterPipeFunction(URI objectUri, Set<URI> propertyUris) {
		this.vf = ValueFactoryImpl.getInstance();
		
		// Set the object filter.
		this.object = objectUri;
		this.predicate = vf.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
		
		// Set property filters (if any).
		if (propertyUris != null) {
			for (URI uri : propertyUris) {
				this.properties.add(uri);
			}
		}
	}
	
	@Override
	public Boolean compute(NamedGraph graph) {
		// First, see if we've got the right object.
		if (!graph.match(null, this.predicate, this.object).hasNext()) {
			return false;
		}
		// Then check the required properties.
		for (URI prop : properties) {
			if (!graph.match(null, prop, null).hasNext()) {
				return false;
			}
		}
		return true;
	}
}
