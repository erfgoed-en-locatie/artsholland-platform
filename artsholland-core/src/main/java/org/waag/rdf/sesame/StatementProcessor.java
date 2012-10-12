package org.waag.rdf.sesame;

import org.openrdf.model.Statement;

public interface StatementProcessor {
	Statement process(EnricherConfig config, Statement statement);
}
