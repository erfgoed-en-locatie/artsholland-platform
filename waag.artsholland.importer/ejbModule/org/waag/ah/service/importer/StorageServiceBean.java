package org.waag.ah.service.importer;

import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/store")})
public class StorageServiceBean {
	private Logger logger = Logger.getLogger(StorageServiceBean.class);

	public void onMessage(Message msg) throws IOException, JMSException {
		String messageText = ((TextMessage)msg).getText();
		logger.info("Storing RDF document: size="+messageText.length());
	}
}