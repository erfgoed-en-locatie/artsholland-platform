//package org.waag.ah.service.importer;
//
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.concurrent.Future;
//
//import javax.ejb.AsyncResult;
//import javax.ejb.Asynchronous;
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//import javax.jms.BytesMessage;
//
//import org.apache.log4j.Logger;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.parser.AutoDetectParser;
//import org.waag.ah.DocumentWriter;
//import org.waag.ah.jms.Properties;
//import org.waag.ah.tika.parser.sax.StreamingContentHandler;
//
//import com.Ostermiller.util.CircularByteBuffer;
//
//
//@Stateless
//public class AsyncParserBean {
//	private Logger logger = Logger.getLogger(AsyncParserBean.class);
//	
//	@EJB
//	private StreamingMessageHelper streamHelper;
//	
//	@Asynchronous
//	public Future<Boolean> parse(BytesMessage message, DocumentWriter writer) {
//		logger.info("parseQueueMessage START");
//		InputStream inputStream = null;
//		CircularByteBuffer buffer = new CircularByteBuffer();
//		inputStream = buffer.getInputStream();
//		Future<Boolean> task = null;
//		try {
//			task = streamHelper.outputStreamToInputStream(message, buffer);
//			Metadata metadata = new Metadata();
//			metadata.add(Metadata.RESOURCE_NAME_KEY, 
//					message.getStringProperty(Properties.SOURCE_URL));	
//			metadata.add(Metadata.CONTENT_ENCODING, 
//					new InputStreamReader(inputStream).getEncoding());
//			
//			logger.info(metadata);
//			
//			StreamingContentHandler handler = 
//					new StreamingContentHandler(writer, metadata);
//			AutoDetectParser tikaParser = new AutoDetectParser();
//			tikaParser.parse(inputStream, handler, metadata);
//			
//			logger.info("parseQueueMessage FINISH");
//			
//			return new AsyncResult<Boolean>(true);
//		} catch (Exception e) {
//			logger.error("Error parsing message: "+e.getMessage(), e);
//			return new AsyncResult<Boolean>(false);
//		} finally {
//			try {
//				if (!task.isDone()) {
//					logger.info("parseQueueMessage KILLING TASK");
//					task.cancel(true);
//				}
//				buffer.clear();				
//				message.acknowledge();
////				inputStream.close();
//			} catch (Exception e) {
//				logger.error("parseQueueMessage WRAPUP ERROR: "+e.getMessage(), e);
//			}
//		}
//	}
//}