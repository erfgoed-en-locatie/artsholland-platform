package org.waag.ah.spring.view;

import java.util.Map;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractView;
import org.waag.ah.QueryService;
import org.waag.ah.QueryTask;
import org.waag.ah.rdf.RDFWriterConfig;
import org.waag.ah.rdf.RdfQueryDefinition;

public class QueryTaskView extends AbstractView {
	final static Logger logger = LoggerFactory.getLogger(QueryTaskView.class);
	public static final String DEFAULT_CONTENT_TYPE = "application/json";
	
//	@Autowired
	private QueryService queryService;
	
//	@Autowired
//	private PropertiesConfiguration platformConfig;
	
	public QueryTaskView() {
		setContentType(DEFAULT_CONTENT_TYPE);
		setExposePathVariables(false);
	}
	
	public void setQueryService(QueryService queryService) {
		Assert.notNull(queryService, "'queryService' must not be null");
		this.queryService = queryService;
	}
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Assert.isTrue(model.containsKey("rdfQueryDefinition"));
		RdfQueryDefinition query = (RdfQueryDefinition) model.get("rdfQueryDefinition");
		
		RDFWriterConfig config = query.getWriterConfig();	
		config.setFormat(getContentType());
		
		try {
			QueryTask queryTask = queryService.getQueryTask(query,
					query.getWriterConfig(), response.getOutputStream());
			if (query.getCountQuery() != null) {
				config.setMetaData("count", queryTask.getCount());
			}
			response.setStatus(HttpServletResponse.SC_OK);
			FutureTask<Void> ft = queryService.executeQueryTask(queryTask);
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
}
