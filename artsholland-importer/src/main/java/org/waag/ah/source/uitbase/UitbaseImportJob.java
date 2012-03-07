package org.waag.ah.source.uitbase;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UitbaseImportJob implements Job {
	private Logger logger = LoggerFactory.getLogger(UitbaseImportJob.class);
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.info("Execute UITBASE import");
	}
}
