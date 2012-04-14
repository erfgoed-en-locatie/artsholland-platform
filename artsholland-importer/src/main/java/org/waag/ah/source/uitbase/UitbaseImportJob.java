package org.waag.ah.source.uitbase;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.joda.time.DateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.ImportMetadata;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.PlatformConfigHelper.PlatformConfig;
import org.waag.ah.importer.AbstractImportJob;

@DisallowConcurrentExecution
public class UitbaseImportJob extends AbstractImportJob {
	private Logger logger = LoggerFactory.getLogger(UitbaseImportJob.class);
	private URL resource;
	private String strategy;
	
	private PlatformConfig config;
	private UitbaseURLGenerator urlGenerator = null;
	
	public UitbaseImportJob() {
		try {
			config = PlatformConfigHelper.getConfig();
			urlGenerator = new UitbaseURLGenerator(
					config.getString("importer.source.uitbase.v4.endpoint"),
					config.getString("importer.source.uitbase.v4.apiKey"));
		} catch (ConfigurationException e) {
			logger.error("Error loading Uitbase configuration");
		}
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
//		List<JobExecutionContext> jobs = context.getScheduler()
//				.getCurrentlyExecutingJobs();
//		for (JobExecutionContext job : jobs) {
//			if (job.getTrigger().equals(context.getTrigger())
//					&& !job.getJobInstance().equals(this)) {
//				logger.info("There's another instance running, so leaving"
//						+ this);
//				return;
//			}
//		}		
		
		if (urlGenerator == null) {
			throw new JobExecutionException(
					"Cannot execute job: Uitbase config not loaded");
		}
		
		DateTime dt = new DateTime(2012, 4, 1, 0, 0, 0, 0);//DateTimeZone.forID("Europe/Amsterdam")
		long ts = dt.getMillis();
		
		JobKey key = context.getJobDetail().getKey();
//		JobDataMap dataMap = context.getMergedJobDataMap();
		
		logger.info("Executing UITBASE import job");
		logger.info("\tKey: "+key.toString());
		logger.info("\tURL: "+this.resource.toExternalForm());
		logger.info("\tStrategy: "+this.strategy);
		logger.info("\tDatetime: "+dt);
		logger.info("\tTimestamp: "+ts);
		
		ImportMetadata metadata = new ImportMetadata();
		metadata.setJobIdentifier(context.getFireInstanceId());

//		DBCollection coll = mongo.getCollection(ImportJob.class.getName());
		
//		coll.drop();
//		ImportJob e1 = new ImportJob();
//		e1.put("source", UitbaseImportJob.class.getName());
//		e1.put("jobId", context.getFireInstanceId());
//		e1.put("timestamp", ts);
//		e1.put("strategy", this.strategy);
//		coll.insert(e1);
//		ImportJob e2 = new ImportJob();
//		e2.put("source", UitbaseImportJob.class.getName());
//		e2.put("jobId", context.getFireInstanceId());
//		e2.put("timestamp", ts);
//		e2.put("strategy", this.strategy);
//		coll.insert(e2);
//		ImportJob e3 = new ImportJob();
//		e3.put("source", UitbaseImportJob.class.getName());
//		e3.put("jobId", context.getFireInstanceId());
//		e3.put("timestamp", ts);
//		e3.put("strategy", this.strategy);
//		coll.insert(e3);
        
//        if(cur.hasNext()) {
//            logger.info(cur.next().toString());
//        } else {
//        	logger.info("NO RECORDS");
//        }
		
		try {
			List<URL> urls = urlGenerator.getURLs(dt);
			logger.info(urls.toString());
			doImport(urls, metadata);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		} finally {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("lastImported", dt);
		}
	}
	
    public void setResource(String url) throws MalformedURLException {
        this.resource = new URL(url);
    }	
    
    public void setStrategy(String strategy) {
    	this.strategy = strategy;
    }
//    private static class UitbaseImportJobStatus implements Serializable {
//    	private long lastImported;
//
//		public void setLastImported(long lastImported) {
//    		this.lastImported = lastImported;
//    	}
//		
//		public DateTime getLastImported() {
//			DateTime dt = new DateTime();
//		}
//    }
}
