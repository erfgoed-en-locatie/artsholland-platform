package org.waag.ah.spring.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.waag.ah.QueryService;
import org.waag.ah.QueryTask;
import org.waag.ah.rdf.RDFWriterConfig;
import org.waag.ah.rdf.RdfQueryDefinition;
import org.waag.ah.rest.util.MIMEParse;

@Service(value="sparqlService")
public class SPARQLService {
	private static final Logger logger = LoggerFactory.getLogger(SPARQLService.class);
	
	@EJB(mappedName="java:app/datastore/BigdataQueryService")
	private QueryService context;

	public static final transient String
		MIME_TEXT_PLAIN 			= "text/plain",
		MIME_APPLICATION_XML 		= "application/xml",
		MIME_APPLICATION_RDF_XML 	= "application/rdf+xml",
		MIME_APPLICATION_JSON 		= "application/json",
		MIME_APPLICATION_RDF_JSON 	= "application/rdf+json",
		MIME_SPARQL_RESULTS_XML 	= "application/sparql-results+xml",
		MIME_SPARQL_RESULTS_JSON 	= "application/sparql-results+json";
	
	private static List<String> allowedFormats = Arrays.asList(
			MIME_APPLICATION_XML,
			MIME_APPLICATION_RDF_XML,
			MIME_APPLICATION_JSON,
			MIME_APPLICATION_RDF_JSON,
			MIME_SPARQL_RESULTS_JSON,
			MIME_SPARQL_RESULTS_XML);
	
	private static Map<String, String> mappedFormats = new HashMap<String, String>();
	static {
		mappedFormats.put(MIME_APPLICATION_JSON, MIME_SPARQL_RESULTS_JSON);
		mappedFormats.put(MIME_APPLICATION_RDF_JSON, MIME_SPARQL_RESULTS_JSON);
		mappedFormats.put(MIME_APPLICATION_XML, MIME_SPARQL_RESULTS_XML);
		mappedFormats.put(MIME_APPLICATION_RDF_XML, MIME_SPARQL_RESULTS_XML);
	}

	public void query(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String mimeType = MIMEParse.bestMatch(allowedFormats, request.getHeader("Accept"));
        
        if (mappedFormats.containsKey(mimeType)) {
        	mimeType = mappedFormats.get(mimeType);
        }
        
        if (mimeType.isEmpty()) {
        	response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        	return;
        }
        
        try {
        	RDFWriterConfig config = new RDFWriterConfig();
        	config.setBaseUri(request.getRequestURL().toString());
        	config.setFormat(mimeType);
        	
			RdfQueryDefinition query = new RdfQueryDefinition(
					QueryLanguage.SPARQL, request.getParameter("query"));
        	
			final QueryTask queryTask = context.getQueryTask(query, config,
					response.getOutputStream());

            if (logger.isTraceEnabled()) {
                logger.trace("Will run query: " + query);
            }

            FutureTask<Void> ft = context.executeQueryTask(queryTask);
            response.setStatus(HttpServletResponse.SC_OK);
            ft.get();
		} catch (MalformedQueryException ex) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					ex.getLocalizedMessage());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getLocalizedMessage());
		}
	}
}
