package org.waag.ah.enrich;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.openrdf.model.Statement;
import org.openrdf.query.GraphQueryResult;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.QueryService;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.rdf.GraphEnricher;
import org.waag.ah.tinkerpop.pipe.EnricherPipeline;

import com.tinkerpop.pipes.util.Pipeline;

public class EnrichObjectJob implements Job {
	final static Logger logger = LoggerFactory.getLogger(EnrichObjectJob.class);

	private QueryService queryService;

	private Class<? extends GraphEnricher> enricher;
	private String objectUri;
	private List<String> includeUris = new ArrayList<String>();
	private List<String> excludeUris = new ArrayList<String>();
	
	public EnrichObjectJob() throws NamingException {
		InitialContext ic = new InitialContext();
		queryService = (QueryService) ic
				.lookup("java:global/artsholland-platform/datastore/BigdataQueryService");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		try {
			Assert.assertNotNull("Enricher class cannot be null");
//			Assert.assertNotNull("Object URI cannot be null");

			EnricherConfig config = new EnricherConfig();
			config.setEnricher(this.enricher);
			config.setObjectUri(this.objectUri);
			config.addIncludeUri(this.includeUris);
			config.addExcludeUri(this.excludeUris);

			String queryString = EnrichUtils.getObjectQuery(config, 10);
			GraphQueryResult result = queryService.executeQuery(queryString);

			List<Statement> statements = new ArrayList<Statement>();
			while (result.hasNext()) {
				statements.add(result.next());
			}
//			logger.info(statements.toString());
			
			Pipeline<Statement, Statement> pipeline = new EnricherPipeline();
			pipeline.setStarts(statements);
			
			while(pipeline.hasNext()) {
				System.out.println("OUT: "+pipeline.next());
			}			
		} catch (IndexOutOfBoundsException e) {
			return;
		} catch (Exception e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
	
	public void setEnricherClass(String className) throws JobExecutionException {
		try {
			this.enricher = Class.forName(className).asSubclass(GraphEnricher.class);
		} catch (ClassNotFoundException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
	
	public void setObjectUri(String uri) {
		this.objectUri = uri;
	}
	
	public void setIncludeProperties(String props) {
		this.includeUris = cleanupUris(props);
	}
	
	public void setExcludeProperties(String props) {
		this.includeUris = cleanupUris(props);
	}
	
	private List<String> cleanupUris(String uris) {
		List<String> uriList = new ArrayList<String>();
		for (String uri : Arrays.asList(uris.split(","))) {
			uriList.add(uri.replaceAll("\\r|\\n| ", ""));
		}
		return uriList;
	}
}
