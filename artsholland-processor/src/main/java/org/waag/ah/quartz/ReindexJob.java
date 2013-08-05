package org.waag.ah.quartz;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.importer.ImportResult;
import org.waag.ah.service.MongoConnectionService;
import org.waag.ah.service.RepositoryConnectionFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class ReindexJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(ReindexJob.class);

	private RepositoryConnectionFactory cf;
	private DBCollection coll;
	
	public ReindexJob() throws NamingException, ConnectionException, RepositoryException {
		InitialContext ic = new InitialContext();
		cf = (RepositoryConnectionFactory) ic
				.lookup("java:global/artsholland-platform/core/RepositoryConnectionService");
		
		MongoConnectionService mongo = (MongoConnectionService) ic
				.lookup("java:global/artsholland-platform/datastore/MongoConnectionServiceImpl");
		this.coll = mongo.getCollection(UrlImportJob.class.getName());
		coll.setObjectClass(ImportResult.class);
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		String jobKey = context.getJobDetail().getKey().toString();
		ImportResult lastResult = getLastResult(jobKey);
		if (lastResult != null) {
			context.setResult("Import job "+jobKey+" with strategy ONCE already executed, skipping.");
			return;
		}
		
		try {
			// Execute only once. Unschedule after first execution:
			context.getScheduler().unscheduleJob(context.getTrigger().getKey());
			
			logger.info("Reindexing!!!");
			
			RepositoryConnection conn = cf.getReadWriteConnection();
			ValueFactory vf = conn.getValueFactory();
			
			Statement st = vf.createStatement(
					vf.createURI("cmd:##reindex"),
					vf.createURI("cmd:##reindex"), 
					vf.createURI("cmd:##reindex"));
			conn.add(st);
			conn.commit();
			
			conn.close();
		} catch (RepositoryException e) {
			logger.info("Reindexing failed...");
		} catch (SchedulerException e) {
			logger.info("Unscheduling failed...");
		}
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
