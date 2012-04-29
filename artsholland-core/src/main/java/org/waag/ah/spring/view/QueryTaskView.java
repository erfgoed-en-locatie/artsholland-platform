package org.waag.ah.spring.view;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.waag.ah.QueryService;
import org.waag.ah.QueryTask;
import org.waag.ah.rdf.RDFWriterConfig;
import org.waag.ah.rdf.RdfQueryDefinition;

public class QueryTaskView extends AbstractView {
	final static Logger logger = LoggerFactory.getLogger(QueryTaskView.class);
	static final UrlPathHelper urlPathHelper = new UrlPathHelper();
	public static String MODEL_QUERY = "queryDefinition";
	public static final String DEFAULT_TYPE = "json";

	private QueryService queryService;

	private static Map<String, String> supportedFormats = new HashMap<String, String>();
	static {
		supportedFormats.put("json", "application/json");
		supportedFormats.put("rdf", "application/rdf");
		supportedFormats.put("turtle", "text/turtle");
//		supportedFormats.put("n3", "text/n3"); // Not supported by Bigdata/Sesame
	}
	
	public QueryTaskView(QueryService queryService) {
		this.queryService = queryService;
	}
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Assert.isTrue(model.containsKey(MODEL_QUERY));
		RdfQueryDefinition query = (RdfQueryDefinition) model.get(MODEL_QUERY);
		
		String mimeType = getMediaType(request);
		if (mimeType == null) {
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return;
		}

		RDFWriterConfig config = query.getWriterConfig();
		config.setFormat(mimeType);
		response.setContentType(mimeType);
		
		try {
			QueryTask queryTask = queryService.getQueryTask(query,
					query.getWriterConfig(), response.getOutputStream());
			if (query.getCountQuery() != null) {
				config.setMetaData("count", queryTask.getCount());
			}
			response.setStatus(HttpServletResponse.SC_OK);
			FutureTask<Void> ft = new FutureTask<Void>(queryTask);//queryService.executeQueryTask(queryTask);
			ft.run();
			ft.get();
		} catch (MalformedQueryException e) {
			logger.error("BAD QUERY: "+query.getQuery());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage());
		}	
	}
	
	private String getMediaType(HttpServletRequest request) {
		String requestUri = urlPathHelper.getLookupPathForRequest(request);
		String filename = WebUtils.extractFullFilenameFromUrlPath(requestUri);
		String extension = StringUtils.getFilenameExtension(filename);
		if (extension == null) {
			extension = DEFAULT_TYPE;
		}
		return supportedFormats.get(extension);
	}
}
