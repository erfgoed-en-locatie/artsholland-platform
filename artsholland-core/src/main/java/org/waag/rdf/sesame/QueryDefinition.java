package org.waag.rdf.sesame;

import org.openrdf.query.QueryLanguage;
import org.waag.rdf.WriterConfig;

public interface QueryDefinition {
	String getQuery();
	String getCountQuery();
	QueryLanguage getQueryLanguage();
	WriterConfig getWriterConfig();
	boolean isSingle();
}
