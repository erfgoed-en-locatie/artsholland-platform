package org.waag.ah.service.scheduler;
//package org.waag.ah.service.scheduler;
//
//import java.util.Date;
//
//import org.quartz.JobDetail;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.SchedulerFactory;
//import org.quartz.Trigger;
//import org.quartz.TriggerUtils;
//import org.quartz.impl.StdSchedulerFactory;
//
//public class ScheduleImport {
//
//	public ScheduleImport(){
//		try{
//			SchedulerFactory schdFact = new StdSchedulerFactory("quartz.properties");
//			Scheduler schd = schdFact.getScheduler();
//			schd.start();
//
//			JobDetail jd = new JobDetail("alarmjob", Scheduler.DEFAULT_GROUP, AlarmJob.class);
//			Trigger t = TriggerUtils.makeDailyTrigger("alarmtrigger", 06, 00);
//			t.getJobDataMap().put("auth_name", "Vageesh");
//			t.setStartTime(new Date());
//			schd.addJobListener(new AlarmJobListener());
//			jd.addJobListener("Alarm gone");
//			schd.scheduleJob(jd, t);
//			System.out.println(schd.getSchedulerName());
//		}
//		catch(SchedulerException e){
//			e.printStackTrace();
//		}
//	}
//
//	public static void main(String[] args) {
//		new AlarmSchedule();
//	}
//}
