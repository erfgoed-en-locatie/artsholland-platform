package org.waag.ah.source.nbtc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBTransactionRolledbackException;

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
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-test.xml",
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-da_DK.xml",
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-de_DE.xml",
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-en_GB.xml",
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-es_ES.xml",
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-fr_FR.xml",
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-it_IT.xml",
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-nl_NL.xml",
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-ru_RU.xml",
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-sv_SE.xml",
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
		} catch(EJBTransactionRolledbackException e) {
			logger.warn(e.getMessage());
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
