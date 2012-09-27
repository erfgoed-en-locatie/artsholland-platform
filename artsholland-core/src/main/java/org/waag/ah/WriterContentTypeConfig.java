package org.waag.ah;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bigdata.rdf.sparql.ast.QueryType;

public abstract class WriterContentTypeConfig {
	
	// TODO: move content-types to separate class
	public static final transient String
		//("n3", "text/n3")
		MIME_TEXT_PLAIN							= "text/plain",
		MIME_TEXT_TURTLE						= "text/turtle",
		MIME_APPLICATION_XML				= "application/xml",
		MIME_APPLICATION_RDF			  = "application/rdf",
		MIME_APPLICATION_JSON			  = "application/json",
		MIME_APPLICATION_RDF_XML		= "application/rdf+xml",
		MIME_APPLICATION_RDF_JSON		= "application/rdf+json",
		MIME_SPARQL_RESULTS_XML			= "application/sparql-results+xml",
		MIME_SPARQL_RESULTS_JSON		= "application/sparql-results+json",
		MIME_SPARQL_RESULTS_CSV			= "text/csv",
		MIME_AH_REST_JSON						= "application/x-waag-artsholland-restapi+json",
		MIME_APPLICATION_JAVASCRIPT = "application/javascript";
		
	private static List<String> CONTENT_TYPES_JSON = Arrays.asList(
			MIME_SPARQL_RESULTS_JSON,
			MIME_APPLICATION_JSON,
			MIME_APPLICATION_JAVASCRIPT//,
//			MIME_AH_REST_JSON
		);
	
	private static List<String> CONTENT_TYPES_SELECT = Arrays.asList(
		MIME_SPARQL_RESULTS_JSON,
		MIME_SPARQL_RESULTS_XML,
		MIME_SPARQL_RESULTS_CSV,
//		MIME_AH_REST_JSON
		MIME_APPLICATION_JSON
	);
	
	private static List<String> CONTENT_TYPES_CONSTRUCT = Arrays.asList(
			MIME_TEXT_PLAIN,
			MIME_TEXT_TURTLE,
			MIME_APPLICATION_XML,
			MIME_APPLICATION_RDF,
			MIME_APPLICATION_JSON,
			MIME_APPLICATION_RDF_XML,
			MIME_APPLICATION_RDF_JSON
		);
	
	private static Map<String, String> EXTENSION_MAP_SELECT = new HashMap<String, String>();
	static {
		EXTENSION_MAP_SELECT.put("json",	MIME_SPARQL_RESULTS_JSON);
		EXTENSION_MAP_SELECT.put("jsonp",	MIME_SPARQL_RESULTS_JSON);
		EXTENSION_MAP_SELECT.put("rdf",		MIME_SPARQL_RESULTS_XML);
		EXTENSION_MAP_SELECT.put("csv", 	MIME_SPARQL_RESULTS_CSV);
	}
	
	private static Map<String, String> EXTENSION_MAP_CONSTRUCT = new HashMap<String, String>();
	static {
		EXTENSION_MAP_CONSTRUCT.put("json",	  MIME_APPLICATION_JSON);//MIME_AH_REST_JSON);
		EXTENSION_MAP_CONSTRUCT.put("jsonp",  MIME_APPLICATION_JSON);//MIME_AH_REST_JSON);
		EXTENSION_MAP_CONSTRUCT.put("rdf",		MIME_APPLICATION_RDF_XML);
		EXTENSION_MAP_CONSTRUCT.put("turtle", MIME_TEXT_TURTLE);
	}
	
//private static Map<String, String> mappedFormats = new HashMap<String, String>();
//static {
//	mappedFormats.put(MIME_APPLICATION_JSON, MIME_SPARQL_RESULTS_JSON);
//	mappedFormats.put(MIME_APPLICATION_RDF_JSON, MIME_SPARQL_RESULTS_JSON);
//	mappedFormats.put(MIME_APPLICATION_XML, MIME_SPARQL_RESULTS_XML);
//	mappedFormats.put(MIME_APPLICATION_RDF_XML, MIME_SPARQL_RESULTS_XML);
//}	

	public String mapExtension(QueryType queryType, String extension) {
		if (queryType == QueryType.SELECT) {
			return mapExtension(extension, EXTENSION_MAP_SELECT);
		} else if (queryType == QueryType.CONSTRUCT) {
			return mapExtension(extension, EXTENSION_MAP_CONSTRUCT);
		} else {
			return null;
		}
	}
	
	private String mapExtension(String extension,	Map<String, String> extensionMap) {
		if (extensionMap.containsKey(extension)) {
			return extensionMap.get(extension);
		}
		return null;
	}

	public List<String> getSupportedContentTypes(QueryType queryType) {
		if (queryType == QueryType.SELECT) {
			return CONTENT_TYPES_SELECT;
		} else if (queryType == QueryType.CONSTRUCT) {
			return CONTENT_TYPES_CONSTRUCT;
		} else {
			return null;
		}
	}
		
	public String getContentType(QueryType queryType, String extensionType, String acceptType) {
		if (extensionType != null) {
			return extensionType;
		} else if (acceptType != null) {
			return acceptType;
		}
		return getDefaultContentType(queryType);
	}
	
	public abstract String getDefaultContentType(QueryType queryType);

	public static boolean isJSON(String contentType) {
		return CONTENT_TYPES_JSON.contains(contentType);
	}
	
}
