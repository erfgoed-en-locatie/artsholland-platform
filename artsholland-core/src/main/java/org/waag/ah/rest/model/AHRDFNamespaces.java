package org.waag.ah.rest.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AHRDFNamespaces {

	private static final Map<String, String> NAMESPACES = createMap();

	private static Map<String, String> createMap() {
		Map<String, String> result = new LinkedHashMap<String, String>();

		result.put("ah", "http://purl.org/artsholland/1.0/");
		result.put("nub", "http://resources.uitburo.nl/");
		result.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		result.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		result.put("owl", "http://www.w3.org/2002/07/owl#");
		result.put("dc", "http://purl.org/dc/elements/1.1/");
		result.put("foaf", "http://xmlns.com/foaf/0.1/");
		result.put("xsd", "http://www.w3.org/2001/XMLSchema#");
		result.put("time", "http://www.w3.org/2006/time#");
		result.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		result.put("vcard", "http://www.w3.org/2006/vcard/ns#");

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
	
}