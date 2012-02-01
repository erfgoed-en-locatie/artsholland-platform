//package org.waag.ah.service.importer;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.concurrent.Future;
//
//import javax.ejb.AsyncResult;
//import javax.ejb.Asynchronous;
//import javax.ejb.Stateless;
//import javax.jms.BytesMessage;
//import javax.jms.JMSException;
//
//import org.apache.tika.exception.TikaException;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.parser.AutoDetectParser;
//import org.apache.tika.parser.ParseContext;
//import org.apache.tika.sax.ToXMLContentHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.waag.ah.jms.Properties;
//import org.xml.sax.ContentHandler;
//import org.xml.sax.SAXException;
//
//@Stateless
//public class StreamingMessageParser {
//	final static Logger logger = LoggerFactory.getLogger(StreamingMessageParser.class);
//
//	@Asynchronous
//	public Future<Boolean> parseStreamMessage(BytesMessage message, InputStream in, OutputStream out) 
//			throws IOException, SAXException, TikaException, JMSException {
//		try {
//			logger.info("STARTED PARSING");
//			AutoDetectParser parser = new AutoDetectParser();
//			
//			Metadata metadata = new Metadata();
//			metadata.add(Metadata.RESOURCE_NAME_KEY, 
//					message.getStringProperty(Properties.SOURCE_URL));	
//			metadata.add(Metadata.CONTENT_ENCODING,
//					message.getStringProperty(Properties.CHARSET));
//			
//			ContentHandler handler = new ToXMLContentHandler(out,"UTF-8"); 
////					metadata.get(Metadata.CONTENT_ENCODING));
//			
//			parser.parse(in, handler, metadata, new ParseContext());
//			
//			logger.info("FINISHED PARSING");
//			
////			in.close();
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		} finally {
//			logger.info("CLOSING STREAMS");
////			in.close();
//			out.close();
//		}
//		logger.info("RETURNING");
//		return new AsyncResult<Boolean>(true);
//	}
//
//	@Asynchronous
//	public Future<Boolean> parse(InputStream in, OutputStream out) 
//			throws IOException, SAXException, TikaException, JMSException {
//		return parse(in, out, new Metadata());
//	}
//	
//	@Asynchronous
//	public Future<Boolean> parse(InputStream in, OutputStream out, Metadata metadata) 
//			throws IOException, SAXException, TikaException, JMSException {
//		AutoDetectParser parser = new AutoDetectParser();
//		ContentHandler handler = new ToXMLContentHandler(out,"UTF-8"); 
//		parser.parse(in, handler, metadata, new ParseContext());
//		return new AsyncResult<Boolean>(true);
//	}
//}