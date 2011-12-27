package org.waag.artsholland.service.importer;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;


@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/import"),
		@ActivationConfigProperty(propertyName="messageSelector", propertyValue="url IS NOT NULL")})
public class ImporterServiceBean implements ImporterService {
	private Logger logger = Logger.getLogger(ImporterServiceBean.class);
	
	public void importSource(String url) {
		logger.info("Importing URL: "+url);
		
//		try {
			// Should use somwe kind of wrapper here which handles
			// authentication on the remote endpoint.
//			URLConnection conn = new URL(url).openConnection();
//			Map<String, List<String>> header = conn.getHeaderFields();
//			logger.info(header);
//			
//			StreamingContentHandler handler = new StreamingContentHandler(System.out); 
//			InputStream stream = conn.getInputStream();
//			Metadata metadata = new Metadata();
//			Parser parser = new AutoDetectParser();
//			ParseContext parseContext = new ParseContext();
//			
//			parser.parse(stream, handler, metadata, parseContext);
//			stream.close();
			
		// All exceptions should provide some feedback to the message sender.
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (SAXException e) {
//			e.printStackTrace();
//		} catch (TikaException e) {
//			e.printStackTrace();
//		}
	}

	public void onMessage(Message msg) {
		try {
			importSource(msg.getStringProperty("url"));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
