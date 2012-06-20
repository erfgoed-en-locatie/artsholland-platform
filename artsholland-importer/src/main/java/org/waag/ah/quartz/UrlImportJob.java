package org.waag.ah.quartz;

import java.net.URL;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.joda.time.DateTime;
import org.openrdf.model.Literal;
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
import org.waag.ah.importer.ImportResult;
import org.waag.ah.importer.ImportStrategy;
import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.UrlGenerator;
import org.waag.ah.service.MongoConnectionService;
import org.waag.ah.tinkerpop.pipe.ImporterPipeline;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.tinkerpop.pipes.util.Pipeline;

@DisallowConcurrentExecution
public class UrlImportJob implements Job {
	final static Logger logger = LoggerFactory.getLogger(UrlImportJob.class);

	private DBCollection coll;
	private RepositoryConnection conn;
	private ValueFactory vf;
	private URI source;

	private UrlGenerator urlGenerator;
	private ImportStrategy strategy;

	public UrlImportJob() throws NamingException, ConnectionException {
		InitialContext ic = new InitialContext();
		
		MongoConnectionService mongo = (MongoConnectionService) ic
				.lookup("java:global/artsholland-platform/datastore/MongoConnectionServiceImpl");
		this.coll = mongo.getCollection(UrlImportJob.class.getName());
		coll.setObjectClass(ImportResult.class);
		
		RepositoryConnectionFactory cf = (RepositoryConnectionFactory) ic
				.lookup("java:global/artsholland-platform/datastore/BigdataConnectionService");
		this.conn = cf.getConnection();
		
		this.vf = conn.getValueFactory();
		this.source = vf.createURI("http://purl.org/artsholland/1.0/metadata/source");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		ImportResult result = new ImportResult();
		result.put("jobKey", context.getJobDetail().getKey().toString());
		result.put("jobId", context.getFireInstanceId());
		result.put("timestamp", context.getFireTime().getTime());
		result.put("strategy", this.strategy.toString());
		
		
		try {
			ImportConfig config = new ImportConfig();
			config.setId(context.getFireInstanceId());
			config.setStrategy(this.strategy);
			config.setFromImportDate(getStartTime(context.getJobDetail().getKey().toString()));
			config.setUntilImportDate(new DateTime(context.getFireTime().getTime()));

			logger.info("Running import job with strategy: "+config.getStrategy());

			Literal id = vf.createLiteral(config.getId());
			long oldsize = conn.size();

			Pipeline<URL, Statement> pipeline = new ImporterPipeline(config);
			pipeline.setStarts(urlGenerator.getUrls(config));

			while (pipeline.hasNext()) {
				Statement statement = getContextStatement(pipeline.next());
				conn.add(statement);
				conn.add(statement.getContext(), source, id);				
			}

			conn.commit();

			logger.info("Import comitted, added " + (conn.size() - oldsize) + " statements");
			result.put("success", true);

		} catch (Exception e) {
			logger.warn("JOB FAILURE: " + e.getMessage());
			result.put("success", false);
			try {
				conn.rollback();
			} catch (RepositoryException e1) {
				logger.warn("Error rolling back transaction");
			}
			throw new JobExecutionException(e.getMessage(), e);
		} finally {
			coll.insert(result);
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

	public void setStrategy(String strategy) {
		this.strategy = ImportStrategy.fromValue(strategy);
	}

	private DateTime getStartTime(String jobKey) throws NamingException {
		BasicDBObject query = new BasicDBObject();
		query.put("jobKey", jobKey);
		query.put("success", true);

		DBCursor cur = coll.find(query)
				.sort(new BasicDBObject("timestamp", -1));

		if (cur.hasNext()) {
			ImportResult lastResult = (ImportResult) cur.next();
			return new DateTime(lastResult.get("timestamp"));
		}

		return null;
	}
	
	private Statement getContextStatement(Statement st) {
		return vf.createStatement(st.getSubject(), st.getPredicate(), 
				st.getObject(), vf.createBNode());
	}
}
