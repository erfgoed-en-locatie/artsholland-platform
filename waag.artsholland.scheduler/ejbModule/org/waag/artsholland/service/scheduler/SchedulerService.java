package org.waag.artsholland.service.scheduler;

import javax.ejb.Schedule;

public interface SchedulerService {
	
    @Schedule
    public void automaticTimeout();
    
    public String getLastScheduledImport();
    
//    @Timeout
//    public void programmaticTimeout(Timer timer);
}
