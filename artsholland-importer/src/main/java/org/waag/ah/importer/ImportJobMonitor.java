package org.waag.ah.importer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
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
import org.waag.ah.SchedulerService;

@Startup
@Singleton
public class ImportJobMonitor extends JobListenerSupport {
	final static Logger logger = LoggerFactory
			.getLogger(ImportJobMonitor.class);

	@Resource(name = "java:app/scheduler/QuartzSchedulerService")
	private SchedulerService schedulerService;

	private Scheduler scheduler;
	public static String NAME = ImportJobMonitor.class.getName();

	@PostConstruct
	public void registerListener() {
		try {
			logger.debug("Registering import job listener");
			scheduler = schedulerService.getScheduler();
			scheduler.getListenerManager().addJobListener(this,
					GroupMatcher.jobGroupEquals("WebResourceImport"));
		} catch (SchedulerException e) {
			logger.error("Error registering import job monitor", e);
		}
	}
	
	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		logger.info("JOB EXECUTED: "+context.getFireInstanceId());
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
