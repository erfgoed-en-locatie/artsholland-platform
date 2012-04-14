package org.waag.ah.service;

import org.quartz.Scheduler;
import org.waag.ah.Service;

public interface SchedulerService extends Service {
	Scheduler getScheduler();
}
