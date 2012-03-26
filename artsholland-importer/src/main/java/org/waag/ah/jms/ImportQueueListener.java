package org.waag.ah.jms;

import java.net.URL;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.importer.ImportService;

@MessageDriven(
	activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/parse"),
		@ActivationConfigProperty(propertyName="maxSession", propertyValue = "1")})
public class ImportQueueListener implements MessageListener {
	private static final Logger logger = LoggerFactory.getLogger(ImportQueueListener.class);
	
	@Resource(name = "java:module/ImportService")
	private ImportService importService;
	
	public void onMessage(Message msgIn) {
		try {
			URL url = new URL(((TextMessage)msgIn).getText());
			logger.debug("Received URL import message: "+url.toExternalForm());
//			importService.importURL(url);
		} catch (Exception e) {
			logger.error("Error importing URL", e);
		}
	}
}
