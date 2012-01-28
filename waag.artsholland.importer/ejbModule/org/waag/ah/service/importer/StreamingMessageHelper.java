package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.jms.BytesMessage;
import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.waag.ah.jms.Properties;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.Ostermiller.util.CircularByteBuffer;

@Stateless
@Asynchronous
public class StreamingMessageHelper {
	private Logger logger = Logger.getLogger(StreamingMessageHelper.class);
	
	public Future<Boolean> readOutputStream(BytesMessage message, 
			CircularByteBuffer buffer) throws JMSException {
		message.setObjectProperty("JMS_HQ_SaveStream", buffer.getOutputStream());
		return new AsyncResult<Boolean>(true);
	}
	
	public Future<Boolean> parseStreamMessage(BytesMessage message, InputStream in, OutputStream out) 
			throws IOException, SAXException, TikaException, JMSException {
		try {
			AutoDetectParser parser = new AutoDetectParser();
			
			Metadata metadata = new Metadata();
			metadata.add(Metadata.RESOURCE_NAME_KEY, 
					message.getStringProperty(Properties.SOURCE_URL));	
			metadata.add(Metadata.CONTENT_ENCODING,
					message.getStringProperty(Properties.CHARSET));
			
			ContentHandler handler = new ToXMLContentHandler(
					out, metadata.get(Metadata.CONTENT_ENCODING));
			
			parser.parse(in, handler, metadata, new ParseContext());
		} finally {
			in.close();
			out.close();
		}
		return new AsyncResult<Boolean>(true);
	}
}