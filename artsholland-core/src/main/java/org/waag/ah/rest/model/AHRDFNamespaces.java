package org.waag.ah.rest.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AHRDFNamespaces {

	private static final Map<String, String> NAMESPACES = createMap();

	private static Map<String, String> createMap() {
		Map<String, String> result = new LinkedHashMap<String, String>();

		result.put("ah", "http://purl.org/artsholland/1.0/");
		//result.put("data", "http://data.artsholland.com/");
		//result.put("nub", "http://resources.uitburo.nl/");
		result.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		result.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		result.put("owl", "http://www.w3.org/2002/07/owl#");
		result.put("dc", "http://purl.org/dc/terms/");		
		result.put("foaf", "http://xmlns.com/foaf/0.1/");
		result.put("xsd", "http://www.w3.org/2001/XMLSchema#");
		result.put("time", "http://www.w3.org/2006/time#");
		result.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		result.put("vcard", "http://www.w3.org/2006/vcard/ns#");
		result.put("osgeo", "http://rdf.opensahara.com/type/geo/");
		result.put("bd", "http://www.bigdata.com/rdf/search#");
		result.put("search", "http://rdf.opensahara.com/search#");
		result.put("fn", "http://www.w3.org/2005/xpath-functions#");
		result.put("gr", "http://purl.org/goodrelations/v1#");
		result.put("gn", "http://www.geonames.org/ontology#");
		
		return Collections.unmodifiableMap(result);	
	}
	
	private static final String PREFIX = getSPARQLPrefix();
	
	public static String getSPARQLPrefix() {
		String prefix = "";
		for (Map.Entry<String, String> namespace: NAMESPACES.entrySet()) {
			prefix += "PREFIX " + namespace.getKey() + ": <" + namespace.getValue() + ">\n";
		}
		return prefix;
	}

	public static String getPrefix() {
		return PREFIX;
	}
	
	public static Map<String, String> getNamespaces() {
		return NAMESPACES;
	}
	
	public static String getJavaScriptNamespaces() {
		String javascript = "var D2R_namespacePrefixes = {\n";
		for (Map.Entry<String, String> entry : getNamespaces().entrySet()) {
			javascript += "\t" + entry.getKey() + ": \"" + entry.getValue() + "\",\n";
		}		
		javascript += "};";		
		return javascript;
	}

	public static String getFullURI(String namespaceURI) {
		String[] split = namespaceURI.split(":");
		if (split.length == 2 && NAMESPACES.containsKey(split[0])) {
				return NAMESPACES.get(split[0]) + split[1];
		}
		
		return null;
	}	
	
}