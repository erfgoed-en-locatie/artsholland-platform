package org.waag.ah.service.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.jboss.logging.Logger;
import org.waag.ah.persistence.StoringRDFParser;
import org.xml.sax.ContentHandler;

import com.gc.iotools.stream.is.InputStreamFromOutputStream;

@MessageDriven(
	activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination", propertyValue="queue/importer/parse"),
		@ActivationConfigProperty(propertyName="maxSession", propertyValue = "1"),
		@ActivationConfigProperty(propertyName="transactionTimeout", propertyValue="900000")})
@DependsOn(value="java:/ConnectionFactory")
public class ParserQueueBean implements MessageListener {
	private static final Logger logger = Logger.getLogger(ParserQueueBean.class.toString());
	private @EJB StoringRDFParser parser;
	
//	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void onMessage(Message msgIn) {
		InputStream stream = null;
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//            public void run() {
//            	stream.close();
//            }
//		}));	
		try {
			URL uri = new URL(((TextMessage)msgIn).getText());
			logger.info("Parsing URI: "+uri.toExternalForm());
			stream = parse(uri);
			parser.parse(stream, uri.toExternalForm());
		} catch (Exception e) {
			e.printStackTrace();
			parser.cancel();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		logger.info("Finished parsing");
	}
	
	private InputStream parse(final URL uri) throws IOException {
		final InputStreamFromOutputStream<String> isos = new InputStreamFromOutputStream<String>() {
			@Override
			public String produce(final OutputStream outStream) throws Exception {
				URLConnection conn = uri.openConnection();
				InputStream inStream = conn.getInputStream();				
				
				Metadata metadata = new Metadata();
				metadata.add(Metadata.RESOURCE_NAME_KEY, uri.toExternalForm());	
				metadata.add(Metadata.CONTENT_ENCODING,
						new InputStreamReader(inStream).getEncoding());
					
				AutoDetectParser parser = new AutoDetectParser();
				ContentHandler handler = new ToXMLContentHandler(outStream, "UTF-8"); 
//							metadata.get(Metadata.CONTENT_ENCODING));
				parser.parse(inStream, handler, metadata, new ParseContext());
				return "OK";
			}
		};
		return isos;
	}
}