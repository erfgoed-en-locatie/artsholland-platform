package org.waag.ah.tika.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/*
 * Class is needed because current version of XSPARQL parser
 * incorrectly parses string literals which contain ":", "<" and ">".
 * 
 * TODO: check if still necessary with future version of XSPARQL
 * See https://redmine.waag.org/issues/6297
 */
public class XSPARQLCharacterEncoder {

	// (: translate($arg, '":<>', "'&#58;&#60;$#62;") :)

	private static final String PREFIX = "[[waag_";
	private static final String SUFFIX = "]]";

	private static final Map<String, String> ENCODINGS = createMap();

	private static Map<String, String> createMap() {
		Map<String, String> result = new HashMap<String, String>();

//		result.put("\"", completeEncoding("quote_double"));
//		result.put("http:", completeEncoding("http_prefix"));
		result.put("<", completeEncoding("less_than"));
		result.put(">", completeEncoding("greater_than"));
		result.put("\\", completeEncoding("backslash"));
//		result.put("/", completeEncoding("slash"));
		result.put("https", completeEncoding("http"));

		return Collections.unmodifiableMap(result);
	}

	public static Map<String, String> getEncodings() {
		return ENCODINGS;
	}

	public static String getXQueryReplaceString() {
		return getXQueryReplaceString(ENCODINGS.entrySet().iterator());
	}

	public static String getXQueryReplaceString(
			Iterator<Entry<String, String>> iterator) {
		if (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			return "replace(" + getXQueryReplaceString(iterator) + ", \""
					+ entry.getKey().replace("\\", "\\\\") + "\", \""
					+ entry.getValue() + "\")";
		} else {
			return "$arg";
		}
	}

	private static String completeEncoding(String encoding) {
		return PREFIX + encoding + SUFFIX;
	}

	public static String encode(String text) {
		for (Entry<String, String> encoding : ENCODINGS.entrySet()) {
			text = text.replace(encoding.getKey(), encoding.getValue());
		}
		return text;
	}

	public static String decode(String text) {
		for (Entry<String, String> encoding : ENCODINGS.entrySet()) {

			String key = encoding.getKey();
			if ("\\".equals(key)) {
				text = text.replace(encoding.getValue(), key + key);
			} else {
				text = text.replace(encoding.getValue(), key);
			}

		}
		return text;
	}
	
	/*
	 * XSPARQL parses SPARQL type definitions like ^^xsd:decimal and encloses
	 * those definition with < and > brackets. This leads to Bigdata storing 
	 * invalid type definitions like ^^<xsd:decimal> (a URI enclosed by brackets
	 * means a complete, non-namespaced URI). 
	 * XSPARQL does not allow full URIs as type definition by using brackets.
	 * For now, the repairInvalidTypeURIs function replaces all type definitions
	 * (expecting them all to be namespaced) and removing the brackets.
	 */	
	public static String repairInvalidTypeURIs(String turtle) {
		return turtle.replaceAll("\\^\\^<(.*)>", "^^$1");	
	}
	
	
}
