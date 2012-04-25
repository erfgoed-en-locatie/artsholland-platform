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
import org.waag.ah.service.SchedulerService;

@Startup
@Singleton
public class SchedulerServiceImpl implements SchedulerService {
	final static Logger logger = LoggerFactory.getLogger(SchedulerService.class);
	private Scheduler scheduler;
//	private CascadingClassLoadHelper classLoadHelper;
//	private XMLSchedulingDataProcessor processor;

	@PostConstruct  
	public void init() { 
        try {
        	// Load stored jobs.
        	logger.debug("LOADING QUARTZ JOBS");
            SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");
			scheduler = sf.getScheduler();
			scheduler.start();
			
//	        classLoadHelper = new CascadingClassLoadHelper();
//	        classLoadHelper.initialize();
//	        processor = new XMLSchedulingDataProcessor(this.classLoadHelper);
	        
		} catch (SchedulerException e) {
			e.printStackTrace();
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
		}
	} 
	
	@PreDestroy     
	public void cleanUp() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}    
	
	@Override
	public final Scheduler getScheduler() {
		return scheduler;
	}
	
//	@Override
//	public void scheduleJobsFromFile(String fileName) {
//        try {
//            processor.processFileAndScheduleJobs(fileName, scheduler);
//        } catch (Exception e) {
//            logger.error("Error scheduling jobs: " + e.getMessage(), e);
//        }
//	}
}

