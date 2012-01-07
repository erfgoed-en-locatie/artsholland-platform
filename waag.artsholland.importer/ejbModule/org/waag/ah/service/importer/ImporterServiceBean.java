package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.PostConstruct;
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
import org.waag.ah.tika.parser.sax.StreamingContentHandler;
import org.xml.sax.ContentHandler;


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
	private ContentHandler handler;
	private JmsQueueWriter queueWriter;

	@PostConstruct
	public void create() throws Exception {
		queueWriter = new JmsQueueWriter("queue/store");
		handler = new StreamingContentHandler(queueWriter);
		parser = new AutoDetectParser();
	}

	@PreDestroy
	public void destroy() {
		try {
			queueWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param sourceURL
	 * @throws MalformedURLException
	 * @throws IOException
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Jan 4, 2012
	 * 
	 * @todo Wrap parse exceptions with something less implementation-specific.
	 */
	public void importUrl(URL url) throws IOException {
		URLConnection conn = url.openConnection();
		InputStream stream = conn.getInputStream();

		try {
			Metadata metadata = new Metadata();
			metadata.set(Metadata.CONTENT_ENCODING, 
					new InputStreamReader(stream).getEncoding());
			metadata.set(Metadata.RESOURCE_NAME_KEY, url.toString());

			try {
				parser.parse(stream, handler, metadata);
			} catch (Exception e) {
				throw new IOException(e.getMessage(), e);
			}
			
			logger.debug("Import finished: metadata="+metadata);
		} finally {
			stream.close();
		}
	}

	public void onMessage(Message msg) throws IOException, JMSException {
		importUrl(new URL(((TextMessage)msg).getText()));
	}
}
