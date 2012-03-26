package org.waag.ah.quartz;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.SchedulerService;

@Startup
@Singleton
public class QuartzSchedulerService implements SchedulerService {
	final static Logger logger = LoggerFactory.getLogger(QuartzSchedulerService.class);
	private Scheduler scheduler;

	public QuartzSchedulerService() {
		logger.info("STARTING SCHEDULER SERVICE");
	}

	@PostConstruct  
	public void init() { 
        try {
        	// Load stored jobs.
        	logger.debug("LOADING QUARTZ JOBS");
            SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");
			scheduler = sf.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	} 
	
	public final Scheduler getScheduler() {
		return scheduler;
	}
	
	@PreDestroy     
	public void cleanUp() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}    
}

