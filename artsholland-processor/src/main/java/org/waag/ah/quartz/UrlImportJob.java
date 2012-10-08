package org.waag.ah.quartz;

import java.net.URL;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.ImportResult;
import org.waag.ah.importer.ImportStrategy;
import org.waag.ah.importer.UrlGenerator;
import org.waag.ah.service.MongoConnectionService;
import org.waag.ah.tinkerpop.ImporterPipeline;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.tinkerpop.pipes.util.Pipeline;

@DisallowConcurrentExecution
public class UrlImportJob implements Job {
	final static Logger logger = LoggerFactory.getLogger(UrlImportJob.class);

	private RepositoryConnectionFactory cf;
	private DBCollection coll;

	private UrlGenerator urlGenerator;
	private String graphUri;
	private ImportStrategy strategy = ImportStrategy.FULL;

	public UrlImportJob() throws NamingException, ConnectionException, RepositoryException {
		InitialContext ic = new InitialContext();
		MongoConnectionService mongo = (MongoConnectionService) ic
				.lookup("java:global/artsholland-platform/datastore/MongoConnectionServiceImpl");
		this.coll = mongo.getCollection(UrlImportJob.class.getName());
		coll.setObjectClass(ImportResult.class);
		cf = (RepositoryConnectionFactory) ic
				.lookup("java:global/artsholland-platform/core/ConnectionService");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		ImportResult result = new ImportResult();
		result.put("jobKey", context.getJobDetail().getKey().toString());
		result.put("jobId", context.getFireInstanceId());
		result.put("timestamp", context.getFireTime().getTime());
		result.put("strategy", this.strategy.toString());

		ImportResult lastResult = getLastResult((String) result.get("jobKey"));
		if (lastResult != null && this.strategy == ImportStrategy.ONCE) {
			context.setResult("Import job with strategy ONCE already executed, skipping.");
			return;
		}
		
		try {
			RepositoryConnection conn = cf.getConnection(false);
			ValueFactory vf = conn.getValueFactory();
			URI contextUri = vf.createURI(graphUri);
			
			// TODO: Refactor ImportConfig to a UrlImporterPipeline with
			//       appropriate interface and abstract class to support
			//       all kinds of import inputs (URLs, files, etc).
			ImportConfig config = new ImportConfig();
			
			// TODO: Use context as id?
			config.setId(context.getFireInstanceId());
			config.setStrategy(this.strategy);
			if (lastResult != null) {
				config.setFromDateTime(new DateTime(lastResult.get("timestamp")));
			}
			config.setToDateTime(new DateTime(context.getFireTime().getTime()));
			config.setContext(vf.createURI(graphUri));

			try {
				logger.info("Running import job: strategy="+config.getStrategy());
	
				// TODO: Change ImporterPipeline to receive ImportConfig(s) and 
				//       return ImportResult(s). Persistence must be handled by
				//       the pipeline, not here.
				Pipeline<URL, Statement> pipeline = new ImporterPipeline(config);
				pipeline.setStarts(urlGenerator.getUrls(config));
	
				long oldsize = conn.size(contextUri);
				
//				if (this.strategy == ImportStrategy.FULL) {
//					conn.clear(config.getContext());
//				}
				
				while (pipeline.hasNext()) {
					Statement statement = pipeline.next();
					// TODO: Check if quering for the statement is less
					//       expensive. Maybe repositories should indicate
					//       whether they support delete on insert/update, or
					//       implement a custom RepositoryConnection to handle
					//       these cases.
					conn.remove(statement, contextUri);
					conn.add(statement, contextUri);
				}
				
				conn.commit();
				
				context.setResult("Added "+(conn.size(contextUri)-oldsize)+" statements");
				result.put("success", true);
	
			} catch (Exception e) {
				logger.error("Exception while importing "+contextUri+" ("+e.getMessage()+")");
				conn.rollback();
				result.put("success", false);
				e.printStackTrace();
				throw e; //new ImportException(e.getCause().getMessage());
			} finally {
				coll.insert(result);
				conn.close();
			}
		} catch (Exception e) {
//			throw new JobExecutionException(e);
			JobExecutionException exception = new JobExecutionException(e);
			exception.refireImmediately();
			throw exception;
		}
	}
	
	public void setUrlGeneratorClass(String className)
			throws JobExecutionException {
		try {
			this.urlGenerator = (UrlGenerator) Class.forName(className)
					.newInstance();
		} catch (Exception e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}

	public void setGraphUri(String uri) {
		this.graphUri = uri;
	}
	
	public void setStrategy(String strategy) {
		this.strategy = ImportStrategy.fromValue(strategy);
	}
	
	private ImportResult getLastResult(String jobKey) {
		BasicDBObject query = new BasicDBObject();
		query.put("jobKey", jobKey);
		query.put("success", true);

		DBCursor cur = coll.find(query)
				.sort(new BasicDBObject("timestamp", -1));

		if (cur.hasNext()) {
			return (ImportResult) cur.next();
		}
		
		return null;
	}
}
