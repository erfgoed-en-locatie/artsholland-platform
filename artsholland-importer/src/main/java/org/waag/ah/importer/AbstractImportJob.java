package org.waag.ah.importer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.ImportMetadata;
import org.waag.ah.ImportResource;
import org.waag.ah.ImportService;
import org.waag.ah.PlatformConfigHelper;

public abstract class AbstractImportJob implements Job {
	private Logger logger = LoggerFactory.getLogger(AbstractImportJob.class);
	private ImportService importServiceBean;
	private PropertiesConfiguration config;
	
	public AbstractImportJob() {
		try {
			config = PlatformConfigHelper.getConfig(); 
			Context ctx = new InitialContext();
			importServiceBean = (ImportService) ctx
					.lookup("java:global/artsholland-platform/importer/ImportServiceBean");
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
		} catch (ConfigurationException e) {
			logger.error("Error loading platform config", e);
		}
	}

	protected void doImport(List<URL> urls, ImportMetadata metadata)
			throws Exception {
		metadata.setBaseURI(config.getString("platform.baseUri"));
		List<ImportResource> resources = new ArrayList<ImportResource>();
		for (URL url : urls) {
			resources.add(ImportResourceFactory.getimportResource(url));
		}
		if (urls.size() > 0) {
			importServiceBean.importResource(resources, metadata);
		} else {
			logger.info("No resources to import");
		}
	}
}
