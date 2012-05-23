package org.waag.ah.rdf;

import org.openrdf.query.QueryLanguage;
import org.waag.ah.QueryDefinition;

public class RdfQueryDefinition implements QueryDefinition {
	private final QueryLanguage queryLang;
	private final String query;
	private final String countQuery;
	private boolean single = false;
	private RDFWriterConfig writerConfig = null;
	
	public RdfQueryDefinition(QueryLanguage queryLang, String query) {
		this(queryLang, query, null);
	}
	
	public RdfQueryDefinition(QueryLanguage queryLang, String query, String countQuery) {
		this.queryLang = queryLang;
		this.query = query;
		this.countQuery = countQuery;
	}
	
	public RdfQueryDefinition(QueryLanguage queryLang, String query, String countQuery, boolean single) {
		this.queryLang = queryLang;
		this.query = query;
		this.countQuery = countQuery;
		this.single = single;
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
	
	@Override
	public boolean isSingle() {
		return single;
	}
	
	public void setWriterConfig(RDFWriterConfig config) {
		this.writerConfig = config;
	}
	
	@Override
	public RDFWriterConfig getWriterConfig() {
		return writerConfig;
	}
}
