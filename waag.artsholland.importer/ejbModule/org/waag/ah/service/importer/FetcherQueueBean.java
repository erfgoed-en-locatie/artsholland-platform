package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.waag.ah.DocumentWriter;
import org.waag.ah.jms.Properties;
import org.waag.ah.jms.QueueWriter;

@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/fetch")})
public class FetcherQueueBean {
	private Logger logger = Logger.getLogger(FetcherQueueBean.class);
	private DocumentWriter queueWriter;

	public FetcherQueueBean() throws Exception {
		queueWriter = new QueueWriter("queue/importer/parse");
	}

	@PreDestroy
	public void destroy() {
		try {
			queueWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void importUrl(URL url) throws IOException {
		URLConnection conn = url.openConnection();
		InputStream stream = conn.getInputStream();
		try {
			Map<String, String> metadata = new HashMap<String, String>();
			metadata.put(Properties.SOURCE_URL, url.toString());
			queueWriter.write(stream, metadata);
		} finally {
			stream.close();
		}
	}

	public void onMessage(Message msg) throws IOException, JMSException {
		try {
			URL url = new URL(((TextMessage)msg).getText());
			logger.debug("Fetching URL: "+url);
			importUrl(url);
		} catch (MalformedURLException e) {
			logger.warn(e.getMessage());
		}
	}
}