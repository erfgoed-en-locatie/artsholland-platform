package org.waag.ah.spring.service;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.query.QueryLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waag.ah.WriterContentTypeConfig;
import org.waag.ah.rdf.RDFWriterConfig;
import org.waag.ah.rdf.RdfQueryDefinition;
import org.waag.ah.rdf.SPARQLWriterTypeConfig;
import org.waag.ah.rest.SPARQLParameters;

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
			config.setResponseContentType(WriterContentTypeConfig.MIME_TEXT_PLAIN);
		}
		
		query.setWriterConfig(config);		
		
		return query;
	}
	
}


