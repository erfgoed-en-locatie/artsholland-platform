package org.waag.ah.enrich;

import org.openrdf.model.URI;
import org.waag.ah.rdf.EnricherConfig;

public class EnrichUtils {
	
	public static String getObjectQuery(EnricherConfig config, int limit) {
		
		limit = 250;
		String query = "" +
			"CONSTRUCT { ?object ?p ?o . } \n" +
			"WHERE {\n" +
			"   OPTIONAL { ?object ?p ?o . } \n" +
			"	{\n" +
			"		SELECT DISTINCT ?object \n" +
			"		WHERE { \n" +
			"			[[OBJECT FILTER]] " +
			"			[[INCLUDE FILTER]] " +
			"			[[EXCLUDE FILTER]] " +
			"		} \n" +
			"		GROUP BY ?object \n" +
			"		[[RESULT LIMIT]] " +
			"	} \n" +
			"} \n" +
			"ORDER BY ?object ?p\n";
		
		// Add object filter.
		String objectFilter = config.getObjectUri() != null ? "?object a <"+config.getObjectUri()+"> . \n" : "";
		query = query.replace("[[OBJECT FILTER]]", objectFilter);

		// Add INCLUDE property filter.
		// ?object geo:lat ?lat FILTER (xsd:string(?lat) = \"0.0\")
		String includeProps = "";
		for (URI prop : config.getIncludeUris()) {
			includeProps += "?object <"+prop+"> ?"+Math.abs(prop.toString().hashCode())+" . \n";
		}
		query = query.replace("[[INCLUDE FILTER]]", includeProps);
		
		// Add EXCLUDE property filter.
		// MINUS { ?object <http://purl.org/artsholland/1.0/attachment> ?x } .
		String excludeProps = "";
		for (URI prop : config.getExcludeUris()) {
			excludeProps += "MINUS { ?object <"+prop+"> ?"+Math.abs(prop.toString().hashCode())+" } . \n";
		}
		query = query.replace("[[EXCLUDE FILTER]]", excludeProps);
		
		// Add query result limit.
		query = query.replace("[[RESULT LIMIT]]", (limit > 0 ? "LIMIT "+limit+"\n" : ""));
			
		return query;
	}
}
