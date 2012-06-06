package org.waag.ah.rdf;

import org.openrdf.model.Statement;

public interface StatementProcessor {
	Statement process(EnricherConfig config, Statement statement);
}
