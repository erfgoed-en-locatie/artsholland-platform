package org.waag.ah.spring.service;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.query.QueryLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.rest.SPARQLParameters;
import org.waag.rdf.RDFWriterConfig;
import org.waag.rdf.SPARQLWriterTypeConfig;
import org.waag.rdf.WriterContentTypeConfig;
import org.waag.rdf.sesame.RdfQueryDefinition;

@Service(value="sparqlService")
public class SPARQLService {
	
	@Autowired
	PropertiesConfiguration platformConfig;
	
	public RdfQueryDefinition getQuery(SPARQLParameters params) {
		RdfQueryDefinition query = new RdfQueryDefinition(QueryLanguage.SPARQL, params.getQuery());
		RDFWriterConfig config = new RDFWriterConfig();
		config.setBaseUri(platformConfig.getString("platform.baseUri"));
		config.setContentTypeConfig(new SPARQLWriterTypeConfig());
		config.setJSONPCallback(params.getJSONPCallback());
		if (params.isPlainText()) {
//			config.setResponseContentType(WriterContentTypeConfig.MIME_TEXT_PLAIN);
		} else {
//			config.setResponseContentType(WriterContentTypeConfig.MIME_SPARQL_RESULTS_JSON);
		}
		
		query.setWriterConfig(config);		
		
		return query;
	}
}
