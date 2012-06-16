package org.waag.ah.source.nbtc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.ImportMetadata;
import org.waag.ah.importer.AbstractImportJob;

@DisallowConcurrentExecution
public class NBTCImportJob extends AbstractImportJob {
	private Logger logger = LoggerFactory.getLogger(NBTCImportJob.class);
	
	public static final String[] RESOURCES = { 
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-nl_NL.xml",
	};	

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		try {
			ImportMetadata metadata = new ImportMetadata();
			metadata.setJobIdentifier(context.getFireInstanceId());

			List<URL> urls = getURLs();
			logger.info(urls.toString());
			
			doImport(urls, metadata);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private List<URL> getURLs() throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		for (String resource : RESOURCES) {			
			urls.add(new URL(resource));
		}
		return urls;			
	}
}
