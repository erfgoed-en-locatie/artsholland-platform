package org.waag.ah.spring.view;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.waag.ah.rest.MIMEParse;
import org.waag.rdf.QueryTask;
import org.waag.rdf.RDFWriterConfig;
import org.waag.rdf.WriterContentTypeConfig;
import org.waag.rdf.sesame.QueryService;
import org.waag.rdf.sesame.RDFJSONFormat;
import org.waag.rdf.sesame.RdfQueryDefinition;

import com.bigdata.rdf.sparql.ast.QueryType;

public class QueryTaskView extends AbstractView {
	final static Logger logger = LoggerFactory.getLogger(QueryTaskView.class);
	static final UrlPathHelper urlPathHelper = new UrlPathHelper();
	public static String MODEL_QUERY = "queryDefinition";
	
//	@EJB(mappedName="java:global/artsholland-platform/core/QueryService")
	private QueryService queryService;
	
	private ExecutorService executor;
	
	public QueryTaskView(QueryService queryService) {
		RDFFormat.register(RDFJSONFormat.RESTAPIJSON);
		this.queryService = queryService;
		this.executor = Executors.newCachedThreadPool();
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Assert.isTrue(model.containsKey(MODEL_QUERY));
		RdfQueryDefinition query = (RdfQueryDefinition) model.get(MODEL_QUERY);
		RDFWriterConfig config = query.getWriterConfig();
		WriterContentTypeConfig typeConfig = config.getContentTypeConfig();
		
		try {			
			ParsedQuery parsedQuery = queryService.getParsedQuery(query, config);
			QueryType queryType = queryService.getQueryType(parsedQuery);
			
			String contentType = getRequestedContentType(request, typeConfig, queryType);
			if (contentType == null) {
				response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
				return;
			}			
			
			config.setContentType(contentType);
						
			if (WriterContentTypeConfig.isJSON(contentType) && config.getJSONPCallback() != null && config.getJSONPCallback().length() > 1) {
				config.setJSONP(true);
				contentType = WriterContentTypeConfig.MIME_APPLICATION_JAVASCRIPT;
			} else {
				// Use text/plain when target is browser:
				if (config.isOverrideResponseContentType()) {
					// TODO: Shouldn't config.getContentType() return the overridden value?
					contentType = config.getResponseContentType();	
					config.setContentType(contentType);
				} else if (config.getContentType() == RDFJSONFormat.MIMETYPE) {
					// Or when content-type is our custom "application/x-waag-artsholland-restapi+json",
					// change to default JSON content-type
					contentType = WriterContentTypeConfig.MIME_APPLICATION_JSON;
				}
			}
			
			response.setContentType(contentType + "; charset=UTF-8");
			
			QueryTask queryTask = queryService.getQueryTask(parsedQuery, query,
					config, response.getOutputStream());				
				
			if (query.getCountQuery() != null) {
				config.setMetaData("count", queryTask.getCount());
			}
			response.setStatus(HttpServletResponse.SC_OK);

			Future<Void> ft = executor.submit(queryTask);

			try {
				ft.get(300, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				logger.error("Query execution timeout: " + query.getQuery());
				ft.cancel(true);
				response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT,
						e.getMessage());
			}
			
		} catch (MalformedQueryException e) {
			logger.error("BAD QUERY: " + query.getQuery());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
					.getCause().getMessage());
		}
	}

	// TODO: move to other class?
	private String getRequestedContentType(HttpServletRequest request, WriterContentTypeConfig typeConfig, QueryType queryType) {
		String extension = getExtension(request);		
		String extensionType = typeConfig.mapExtension(queryType, extension);
		
		String acceptHeader = request.getHeader("Accept");
		if (acceptHeader == null) {
			acceptHeader = "*/*";
		}
		
		String acceptType = MIMEParse.bestMatch(typeConfig.getSupportedContentTypes(queryType), acceptHeader);
		
		return typeConfig.getContentType(queryType, extensionType, acceptType);
	}

	public static String getExtension(HttpServletRequest request) {
		String requestUri = urlPathHelper.getLookupPathForRequest(request);
		String filename = WebUtils.extractFullFilenameFromUrlPath(requestUri);
		String extension = StringUtils.getFilenameExtension(filename);
		return extension;
	}
}
