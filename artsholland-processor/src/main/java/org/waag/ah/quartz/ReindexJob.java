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
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.service.RepositoryConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReindexJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(ReindexJob.class);

	private RepositoryConnectionFactory cf;
	
	public ReindexJob() throws NamingException, ConnectionException, RepositoryException {
		InitialContext ic = new InitialContext();
		cf = (RepositoryConnectionFactory) ic
				.lookup("java:global/artsholland-platform/core/RepositoryConnectionService");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
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
}
