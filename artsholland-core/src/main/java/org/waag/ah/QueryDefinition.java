package org.waag.ah;

import org.openrdf.query.QueryLanguage;

public interface QueryDefinition {
	String getQuery();
	String getCountQuery();
	QueryLanguage getQueryLanguage();
	WriterConfig getWriterConfig();
	boolean isSingle();
}
