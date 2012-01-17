package org.waag.ah.service.importer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.waag.ah.persistence.SesameWriter;
import org.waag.ah.tika.parser.sax.StreamingContentHandler;


@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/import")})
//		@ActivationConfigProperty(propertyName="messageSelector", propertyValue="url IS NOT NULL")})
//		@ActivationConfigProperty(propertyName="subscriptionDurability", propertyValue = "Durable")})
//		@ActivationConfigProperty(
//			propertyName = "acknowledgeMode", 
//			propertyValue = "Client-acknowledge"), //Session.CLIENT_ACKNOWLEDGE
//		})
public class ImporterServiceBean implements ImporterService {
	private Logger logger = Logger.getLogger(ImporterServiceBean.class);
	private AutoDetectParser parser;
	private DocumentWriter jmsQueueWriter;

	public ImporterServiceBean() throws IOException {
		logger.info("Start ImporterServiceBean");
		jmsQueueWriter = new SesameWriter();
		parser = new AutoDetectParser();
	}

	@PreDestroy
	public void destroy() {
		try {
			jmsQueueWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void importUrl(URL url) throws IOException {
		logger.info("Start import");
		URLConnection conn = url.openConnection();
		InputStream stream = conn.getInputStream();

		try {
			Metadata metadata = new Metadata();
			metadata.set(Metadata.CONTENT_ENCODING, 
					new InputStreamReader(stream).getEncoding());
			metadata.set(Metadata.RESOURCE_NAME_KEY, url.toString());
			
			try {
				parser.parse(stream, 
						new StreamingContentHandler(jmsQueueWriter, metadata), 
						metadata);
			} catch (Exception e) {
				throw new IOException(e.getMessage(), e);
			}
			
			logger.info("Import finished: metadata="+metadata);
		} finally {
			stream.close();
		}
	}

	public void onMessage(Message msg) throws IOException, JMSException {
		try {
			importUrl(new URL(((TextMessage)msg).getText()));
		} catch (FileNotFoundException e) {
			logger.warn(e.getMessage());
		}
	}
}
