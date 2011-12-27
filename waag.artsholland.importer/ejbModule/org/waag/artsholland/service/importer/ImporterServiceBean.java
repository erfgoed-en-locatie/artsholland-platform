package org.waag.artsholland.service.importer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


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
		
		try {
			// Should use somwe kind of wrapper here which handles
			// authentication on the remote endpoint.
			URLConnection conn = new URL(url).openConnection();
			Map<String, List<String>> header = conn.getHeaderFields();
			logger.info(header);
			
			ContentHandler handler = new BodyContentHandler(System.out);
			InputStream stream = conn.getInputStream();
			Metadata metadata = new Metadata();
			Parser parser = new AutoDetectParser();
			ParseContext parseContext = new ParseContext();
			
			parser.parse(stream, handler, metadata, parseContext);
			
			stream.close();
			
		// All exceptions should provide some feedback to the message sender.
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(Message msg) {
		try {
			importSource(msg.getStringProperty("url"));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
