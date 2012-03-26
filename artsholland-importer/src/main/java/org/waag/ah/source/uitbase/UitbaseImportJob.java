package org.waag.ah.source.uitbase;

import java.net.MalformedURLException;
import java.net.URL;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.ImportMetadata;
import org.waag.ah.importer.AbstractURLImportJob;

public class UitbaseImportJob extends AbstractURLImportJob implements Job {
	private Logger logger = LoggerFactory.getLogger(UitbaseImportJob.class);
	private URL resource;
	private String strategy;
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobKey key = context.getJobDetail().getKey();
//		JobDataMap dataMap = context.getMergedJobDataMap();
		
		logger.info("Executing UITBASE import job");
		logger.info("\tKey: "+key.toString());
		logger.info("\tURL: "+this.resource.toExternalForm());
		logger.info("\tStrategy: "+this.strategy);
		
		ImportMetadata metadata = new ImportMetadata();
		metadata.setJobIdentifier(context.getFireInstanceId());
		
		try {
			doImport(UitbaseURLGenerator.getURLs(), metadata);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}
	
    public void setResource(String url) throws MalformedURLException {
        this.resource = new URL(url);
    }	
    
    public void setStrategy(String strategy) {
    	this.strategy = strategy;
    }

//	@Override
//	protected URL buildResourceURL() throws MalformedURLException {
//		if (strategy.equals(ImportJob.STRATEGY_FULL)) {
//			logger.info("FULL IMPORT");
//		} else if (strategy.equals(ImportJob.STRATEGY_INCREMENTAL)) {
//			logger.info("FULL IMPORT");
//		}
//		ArrayList<String> urls = UitbaseURLGenerator.getURLs();
//		return new URL(urls.get(0));
//	}
}
