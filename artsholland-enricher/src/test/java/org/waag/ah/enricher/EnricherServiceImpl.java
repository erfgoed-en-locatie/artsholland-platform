package org.waag.ah.enricher;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.quartz.Scheduler;
import org.waag.ah.service.EnricherService;
import org.waag.ah.service.SchedulerService;

@Startup
@Singleton
public class EnricherServiceImpl implements EnricherService {
	private Scheduler scheduler;

	private @EJB SchedulerService schedulerService;
	
	@PostConstruct
	public void init() {
		scheduler = schedulerService.getScheduler();
//		jobs = new XMLSchedulingDataProcessor();
	}
}
