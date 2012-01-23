package org.waag.ah.service.importer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.waag.ah.DocumentWriter;
import org.waag.ah.jms.Properties;
import org.waag.ah.jms.QueueWriter;
import org.waag.ah.tika.parser.sax.StreamingContentHandler;

import com.Ostermiller.util.CircularByteBuffer;


@MessageDriven(
	messageListenerInterface=MessageListener.class, 
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/parse")})
public class ParserQueueBean {
	private Logger logger = Logger.getLogger(ParserQueueBean.class);
	private AutoDetectParser parser;
	private DocumentWriter queueWriter;

	public ParserQueueBean() throws Exception {
		queueWriter = new QueueWriter("queue/importer/store");
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
	
	public void parseDocument(InputStream stream, Metadata metadata) 
			throws IOException {
		try {
			metadata.add(Metadata.CONTENT_ENCODING, 
					new InputStreamReader(stream).getEncoding());
			parser.parse(stream, 
					new StreamingContentHandler(queueWriter, metadata), 
					metadata);
		} catch (Exception e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	public void onMessage(Message msg) throws IOException, JMSException {
		Metadata metadata = new Metadata();
		metadata.add(Metadata.RESOURCE_NAME_KEY, 
				msg.getStringProperty(Properties.SOURCE_URL));

		logger.info("Parsing document for URL: "+metadata.get(Metadata.RESOURCE_NAME_KEY));
		
		CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
		BufferedOutputStream bufferedOutput = new BufferedOutputStream(cbb.getOutputStream());
		InputStream inputStream = cbb.getInputStream();
		
		try {
			msg.setObjectProperty("JMS_HQ_SaveStream", bufferedOutput);		
			parseDocument(inputStream, metadata);
		} finally {
			bufferedOutput.close();
			inputStream.close();
		}
	}
}