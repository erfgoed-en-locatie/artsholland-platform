package org.waag.ah.importer;

import java.net.URL;
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
import org.waag.ah.PlatformConfig;
import org.waag.ah.mongo.MongoConnectionService;

public abstract class AbstractImportJob implements Job {
	private Logger logger = LoggerFactory.getLogger(AbstractImportJob.class);
	private ImportService importService;
	private PropertiesConfiguration config;
	protected MongoConnectionService mongo;
	
	public AbstractImportJob() {
		try {
			config = PlatformConfig.getConfig(); 
			Context ctx = new InitialContext();
			importService = (ImportService) ctx
					.lookup("java:global/artsholland-platform/importer/ImportService");
			mongo = (MongoConnectionService) ctx
					.lookup("java:global/artsholland-platform/datastore/MongoConnectionService");
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
		} catch (ConfigurationException e) {
			logger.error("Error loading platform config", e);
		}
	}

	// protected abstract URL buildResourceURL() throws MalformedURLException;

	protected void doImport(List<URL> urls, ImportMetadata metadata)
			throws Exception {
		metadata.setBaseURI(config.getString("platform.baseUri"));
		importService.importURL(urls, metadata);
	}
	
	// @Override
	// public final void doImport() throws JMSException, NamingException,
	// MalformedURLException {
	// QueueConnection connection = factory.createQueueConnection();
	// try {
	// QueueSession session = connection.createQueueSession(false,
	// QueueSession.AUTO_ACKNOWLEDGE);
	// QueueSender sender = session.createSender(queue);
	// String url = buildResourceURL().toExternalForm();
	// TextMessage msg = session.createTextMessage(url);
	// logger.debug("SCHEDULING URL IMPORT: " + url);
	// sender.send(msg);
	// } finally {
	// connection.close();
	// }
	// }
}
