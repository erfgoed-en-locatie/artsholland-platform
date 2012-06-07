//package org.waag.ah.importer;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.ejb.EJB;
//import javax.ejb.Singleton;
//import javax.ejb.Startup;
//
//import org.joda.time.DateTime;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.impl.matchers.GroupMatcher;
//import org.quartz.listeners.JobListenerSupport;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.waag.ah.service.MongoConnectionService;
//import org.waag.ah.service.SchedulerService;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCollection;
//import com.mongodb.DBCursor;
//
///**
// * Monitors import job execution and logs success/failure.
// * 
// * @author Raoul Wissink <raoul@raoul.net>
// */
//@Startup
//@Singleton
//public class ImportJobMonitor extends JobListenerSupport {
//	final static Logger logger = LoggerFactory
//			.getLogger(ImportJobMonitor.class);
//
//	public static String NAME = ImportJobMonitor.class.getName();
//	
//	private Scheduler scheduler;
//	private DBCollection coll;
//	private ImportJobResult lastResult;
//
//	private @EJB SchedulerService schedulerService;
//	private @EJB MongoConnectionService mongoService;
//
//	@PostConstruct
//	public void registerListener() {
//		try {
//			scheduler = schedulerService.getScheduler();
//			scheduler.getListenerManager().addJobListener(this,
//					GroupMatcher.jobGroupEquals("WebResourceImport"));
//			coll = mongoService.getCollection(ImportJobMonitor.class.getName());
//			coll.setObjectClass(ImportJobResult.class);
//		} catch (SchedulerException e) {
//			logger.error("Error registering import job monitor", e);
//		}
//	}
//
//	@PreDestroy
//	public void unregisterListener() {
//		try {
//			scheduler.getListenerManager().removeJobListener(NAME);
//		} catch (SchedulerException e) {
//			logger.error("Error unregistering import job monitor", e);
//		}
//	}
//    
//	@Override
//	public void jobToBeExecuted(JobExecutionContext context) {
//		try {
//        	BasicDBObject query = new BasicDBObject();
//	        query.put("jobKey", context.getJobDetail().getKey().toString());
//	        query.put("success", true);
//	        
//	        DBCursor cur = coll.find(query).sort(new BasicDBObject("timestamp", -1));
//	        
//	        if (cur.hasNext()) {
//	        	lastResult = (ImportJobResult) cur.next();
//	        	context.put("startTime", new DateTime(lastResult.get("timestamp")));
//	        } else {
//	        	context.put("startTime", null);
//	        }
//	        
//	        context.put("endTime", new DateTime(context.getFireTime().getTime()));
//		} catch (Exception e) {
//			logger.error("Error while fetching previous job result: "+e.getMessage());
//		}
//    }
//    
//	@Override
//	public void jobWasExecuted(JobExecutionContext context,
//			JobExecutionException jobException) {
//		if (jobException != null) {
//			logger.info("JOB FAILURE: "+jobException.getUnderlyingException().getMessage());
//		}
//		try {
//			ImportJobResult result = new ImportJobResult();//(ImportJobResult) context.getResult();
//			result.put("jobKey", context.getJobDetail().getKey().toString());
//			result.put("jobClass", context.getJobDetail().getJobClass().getName());
//			result.put("timestamp", context.getFireTime().getTime());
//			result.put("success", jobException == null);
//			coll.insert(result);
//		} catch (Exception e) {
//			logger.error("Exception while storing job result: "+e.getMessage());
//		}
//	}
//
//	@Override
//	public String getName() {
//		return NAME;
//	}
//}
