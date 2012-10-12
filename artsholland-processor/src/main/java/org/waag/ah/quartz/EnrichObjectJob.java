package org.waag.ah.quartz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.openrdf.model.Statement;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.exception.EnrichException;
import org.waag.ah.tinkerpop.EnrichUtils;
import org.waag.ah.tinkerpop.EnricherPipeline;
import org.waag.rdf.sesame.EnricherConfig;
import org.waag.rdf.sesame.GraphEnricher;
import org.waag.rdf.sesame.QueryService;
import org.waag.rdf.sesame.SailConnectionFactory;

import com.tinkerpop.pipes.util.Pipeline;

public class EnrichObjectJob implements Job {
	final static Logger logger = LoggerFactory.getLogger(EnrichObjectJob.class);

	private QueryService queryService;
	private SailConnectionFactory cf;

	private Class<? extends GraphEnricher> enricher;
	private String objectUri;
	private List<String> includeUris = new ArrayList<String>();
	private List<String> excludeUris = new ArrayList<String>();

	public EnrichObjectJob() throws NamingException, ConnectionException, RepositoryException {
		InitialContext ic = new InitialContext();
		queryService = (QueryService) ic
				.lookup("java:global/artsholland-platform/core/QueryService");
		cf = (SailConnectionFactory) ic
				.lookup("java:global/artsholland-platform/core/SailConnectionService");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			SailRepositoryConnection conn = cf.getConnection(false);	
			Assert.assertNotNull("Enricher class cannot be null");
//			Assert.assertNotNull("Object URI cannot be null");

			EnricherConfig config = new EnricherConfig();
			config.setEnricher(this.enricher);
			config.setObjectUri(this.objectUri);
			config.addIncludeUri(this.includeUris);
			config.addExcludeUri(this.excludeUris);
			config.setConnection(conn);
			
			try {
				String queryString = EnrichUtils.getObjectQuery(config, 10);
				GraphQueryResult result = queryService.executeQuery(queryString);
	
				List<Statement> statements = new ArrayList<Statement>();
				while (result.hasNext()) {
					statements.add(result.next());
				}
	//			logger.info(statements.toString());
				
				Pipeline<Statement, Statement> pipeline = new EnricherPipeline(config);
				pipeline.setStarts(statements);
				
				while(pipeline.hasNext()) {
					Statement st = pipeline.next();
//					logger.info(st.toString());
					conn.add(st);
				}
				
				conn.commit();
			} catch (Exception e) {
	//			e.printStackTrace();
				conn.rollback();
				throw new EnrichException(e.getMessage());
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			throw new JobExecutionException(e);
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
		this.excludeUris = cleanupUris(props);
	}
	
	private List<String> cleanupUris(String uris) {
		List<String> uriList = new ArrayList<String>();
		for (String uri : Arrays.asList(uris.split(","))) {
			uriList.add(uri.trim().replaceAll("\\r|\\n| ", ""));
		}
		return uriList;
	}
}
