package org.waag.ah.rdf;

import org.waag.ah.WriterContentTypeConfig;

import com.bigdata.rdf.sparql.ast.QueryType;

public class SPARQLWriterTypeConfig extends WriterContentTypeConfig {
	
	public static final String DEFAULT_CONTENT_TYPE_SELECT    = MIME_SPARQL_RESULTS_JSON;
	public static final String DEFAULT_CONTENT_TYPE_CONSTRUCT = MIME_APPLICATION_RDF_JSON;

	@Override
	public String getDefaultContentType(QueryType queryType) {
		if (queryType == QueryType.SELECT) {
			return DEFAULT_CONTENT_TYPE_SELECT;
		} else if (queryType == QueryType.CONSTRUCT) {
			return DEFAULT_CONTENT_TYPE_CONSTRUCT;
		} else {
			return null;
		}
	}
}
