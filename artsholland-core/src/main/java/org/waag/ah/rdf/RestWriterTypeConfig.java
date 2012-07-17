package org.waag.ah.rdf;

import org.waag.ah.WriterContentTypeConfig;

import com.bigdata.rdf.sparql.ast.QueryType;

public class RestWriterTypeConfig extends WriterContentTypeConfig {

	public static final String DEFAULT_TYPE = MIME_AH_REST_JSON;

	@Override
	public String getContentType(QueryType queryType, String extensionType,
			String acceptType) {

		if (acceptType == MIME_APPLICATION_XML) {
			acceptType = null;
		}
		return super.getContentType(queryType, extensionType, acceptType);
	}

	@Override
	public String getDefaultContentType(QueryType queryType) {
		if (queryType == QueryType.CONSTRUCT) {
			return DEFAULT_TYPE;
		}
		return null;
	}
}
