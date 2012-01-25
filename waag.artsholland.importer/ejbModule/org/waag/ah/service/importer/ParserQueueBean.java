package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
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
	activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/parse")})
public class ParserQueueBean implements MessageListener {
	private Logger logger = Logger.getLogger(ParserQueueBean.class);
	private AutoDetectParser parser;
	private DocumentWriter queueWriter;
	private CircularByteBuffer cbb;

	@EJB
	private AsyncBean async;
	
	@PostConstruct
	public void create() throws Exception {
		queueWriter = new QueueWriter("queue/importer/store");
		parser = new AutoDetectParser();
		cbb = new CircularByteBuffer();
	}

	@PreDestroy
	public void destroy() {
		logger.info("Destroying ParserQueueBean");
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

	/**
	 * Uses a rather cumbersome method for converting convert the JMS 
	 * outputstream to an inputstream.
	 * 
	 * @param msg
	 * @throws IOException
	 * @throws JMSException
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Jan 24, 2012
	 * @see <a href="http://code.google.com/p/io-tools/wiki/ConvertOutputStreamInputStream">here</a>
	 */
	public void onMessage(Message msg) { //throws IOException, JMSException {
		logger.info("onMessage START");
		
//		PipedInputStream inputStream = null;
//		PipedOutputStream outputStream = null;
		Future<String> task = null;
		try {
			Metadata metadata = new Metadata();
			metadata.add(Metadata.RESOURCE_NAME_KEY, 
					msg.getStringProperty(Properties.SOURCE_URL));
	
//			inputStream = new PipedInputStream();
//			outputStream = new PipedOutputStream(inputStream);
			
			logger.info("onMessage COPY");
			task = async.copyOutputStream(msg, cbb);
			logger.info("onMessage COPYDONE");
			parseDocument(cbb.getInputStream(), metadata);
			logger.info("onMessage CLOSE");
		} catch (Exception e) {
			logger.error("PROCESSING ERROR, HANDLE ME!", e);
			if (!task.isDone()) {
				logger.info("CANCELLING TASK");
				task.cancel(true);
			}			
		} finally {
			logger.info("onMessage FINISH");
			cbb.clear();		
		}
	}
}