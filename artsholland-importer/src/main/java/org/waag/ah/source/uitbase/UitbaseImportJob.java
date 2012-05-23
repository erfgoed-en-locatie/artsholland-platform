package org.waag.ah.source.uitbase;

import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.joda.time.DateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.ImportMetadata;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.importer.AbstractImportJob;

@DisallowConcurrentExecution
public class UitbaseImportJob extends AbstractImportJob {
	private Logger logger = LoggerFactory.getLogger(UitbaseImportJob.class);
	private String strategy;

	private PlatformConfig config;
	private UitbaseURLGenerator urlGenerator = null;
	
//	private static String STRATEGY_INCREMENTAL = "incremental";
	private static String STRATEGY_FULL = "full";

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
		if (urlGenerator == null) {
			throw new JobExecutionException(
					"Cannot execute job: Uitbase config not loaded");
		}

		try {
			ImportMetadata metadata = new ImportMetadata();
			metadata.setJobIdentifier(context.getFireInstanceId());

//			JobDataMap dataMap = context.getMergedJobDataMap();
//			logger.info("INCOMING DATAMAP: "+dataMap.getWrappedMap());

			logger.info("Running import job with strategy: "+strategy);
			
			DateTime startTime = strategy.equals(STRATEGY_FULL) ? null
					: (DateTime) context.get("startTime");
			List<URL> urls = urlGenerator.getURLs(
					(DateTime) context.get("endTime"), startTime/*, 
					(DateTime) context.get("startTime")*/);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Importing URLs: "+urls.toString());
			}
			
			doImport(urls, metadata);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
}
