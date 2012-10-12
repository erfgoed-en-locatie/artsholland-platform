package org.waag.rdf.sesame;

import java.util.List;

import org.openrdf.model.Statement;

public interface GraphEnricher {
	List<Statement> enrich(EnricherConfig config, NamedGraph graph);
}
