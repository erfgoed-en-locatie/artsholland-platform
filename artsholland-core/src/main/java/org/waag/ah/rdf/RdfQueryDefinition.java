package org.waag.ah.rdf;

import org.openrdf.query.QueryLanguage;
import org.waag.ah.QueryDefinition;

public class RdfQueryDefinition implements QueryDefinition {
	private final QueryLanguage queryLang;
	private final String query;
	private final String countQuery;
	private RDFWriterConfig writerConfig = null;
	
	public RdfQueryDefinition(QueryLanguage queryLang, String query) {
		this(queryLang, query, null);
	}
	
	public RdfQueryDefinition(QueryLanguage queryLang, String query, String countQuery) {
		this.queryLang = queryLang;
		this.query = query;
		this.countQuery = countQuery;
	}

	@Override
	public QueryLanguage getQueryLanguage() {
		return queryLang;
	}
	
	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public String getCountQuery() {
		return countQuery;
	}
	
	public void setWriterConfig(RDFWriterConfig config) {
		this.writerConfig = config;
	}
	
	@Override
	public RDFWriterConfig getWriterConfig() {
		return writerConfig;
	}
}
