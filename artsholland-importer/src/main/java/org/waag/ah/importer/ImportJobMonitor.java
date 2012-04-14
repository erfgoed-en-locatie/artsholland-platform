package org.waag.ah.importer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.service.SchedulerService;

@Startup
@Singleton
public class ImportJobMonitor extends JobListenerSupport {
	final static Logger logger = LoggerFactory
			.getLogger(ImportJobMonitor.class);
	public static String NAME = ImportJobMonitor.class.getName();

//	@Resource(name = "java:app/scheduler/SchedulerService")
	private @EJB SchedulerService schedulerService;
	
//	@Resource(name = "java:app/datastore/MongoConnectionService")
//	private MongoConnectionService mongo;

	private Scheduler scheduler;
//	private DBCollection coll;

	@PostConstruct
	public void registerListener() {
		try {
			scheduler = schedulerService.getScheduler();
			scheduler.getListenerManager().addJobListener(this,
					GroupMatcher.jobGroupEquals("WebResourceImport"));
//			coll = mongo.getCollection(ImportJobMonitor.class.getName());
		} catch (SchedulerException e) {
			logger.error("Error registering import job monitor", e);
		}
	}
    
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
//		BasicDBObject query = new BasicDBObject();
//        query.put("jobKey", UitbaseImportJob.class.getName());
//        DBCursor cur = coll.find(query).sort(new BasicDBObject("timestamp", -1));
    }
    
	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		if (jobException == null) {
			logger.info("JOB EXECUTED: "+context.getFireInstanceId());
			logSuccess(context);
		} else {
			logger.info("JOB FAILURE: "+context.getFireInstanceId());
		}
	}
	
	private void logSuccess(JobExecutionContext context) {
//		ImportJob e1 = new ImportJob();
//		e1.put("jobKey", context.getJobDetail().getKey());
//		e1.put("jobId", context.getFireInstanceId());
//		e1.put("parameters", context.getMergedJobDataMap());
//		coll.insert(e1);
	}

	@PreDestroy
	public void unregisterListener() {
		try {
			scheduler.getListenerManager().removeJobListener(NAME);
		} catch (SchedulerException e) {
			logger.error("Error unregistering import job monitor", e);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
