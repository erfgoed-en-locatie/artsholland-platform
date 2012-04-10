package org.waag.ah;

import org.openrdf.query.QueryLanguage;
import org.waag.ah.rdf.RDFWriterConfig;

public interface QueryDefinition {
	String getQuery();
	String getCountQuery();
	QueryLanguage getQueryLanguage();
	RDFWriterConfig getWriterConfig();
}
