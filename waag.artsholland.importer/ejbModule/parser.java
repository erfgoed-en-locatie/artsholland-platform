import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


public class parser {
	public static void main(String[] args) {
		
		String sourceURL = "http://waxworks.nl/events.xml";
//		String sourceURL = "http://127.0.0.1/ah/nub/events.xml";
//		String sourceURL = "http://127.0.0.1/ah/nub/amsterdam.xml";
//		String sourceURL = "http://test.publisher.uitburo.nl/agenda/search.do?key=e17c6b21b6852e1ab43abdfdf034f752&locationText=Amsterdam&start=0&rows=500";
//		String sourceURL = "http://127.0.0.1/ah/dos/2011_jaarboek_1816.xls";

		try {
//			FileWriter fstream = new FileWriter("out.txt");
//			BufferedWriter out = new BufferedWriter(fstream);
//			BufferedWriter out = new BufferedWriter(System.out);
			
			URLConnection conn = new URL(sourceURL).openConnection();
			InputStream stream = conn.getInputStream();
			
			final Metadata metadata = new Metadata();
			metadata.set(Metadata.RESOURCE_NAME_KEY, sourceURL);
			
			InputStreamReader r = new InputStreamReader(stream);
			metadata.set(Metadata.CONTENT_ENCODING, r.getEncoding());

//			ContentHandler handler = new JMSQueueContentHandler("queue/store", metadata);
//			new MatchingContentHandler(
//					new JmsQueueContentHandler(metadata),
//					new XPathParser("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#").parse("//rdf:RDF")); 
			
//			ContentHandler handler = new ToTextContentHandler(System.out); 
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			ContentHandler handler = new ToXMLContentHandler(outputStream, Metadata.CONTENT_ENCODING); 
//			StreamingContentHandler handler = new StreamingContentHandler();
			
//			ContentHandler handler = new ToXMLContentHandler(outputStream, 
//					metadata.get(Metadata.CONTENT_ENCODING)); 
//			ContentHandler handler = new ToXMLContentHandler(metadata.get(Metadata.CONTENT_ENCODING)); 
			
//			final PipedInputStream inputStream = new PipedInputStream();
//			PipedOutputStream outputStream = new PipedOutputStream(inputStream);
//			new Thread(
//			    new Runnable() {
//			    	public void run() {
//			    		// Here we write data to JMS.
//			    		try {
//				    		final char[] buffer = new char[0x10000];
//				    		StringBuilder out = new StringBuilder();
//				    		Reader in = new InputStreamReader(inputStream, metadata.get(Metadata.CONTENT_ENCODING));
//				    		int read;
//				    		do {
//								read = in.read(buffer, 0, buffer.length);
//								if (read > 0) {
//									System.out.println("Writing data...");
//									out.append(buffer, 0, read);
//								}
//				    		} while (read>=0);
//				    		System.out.println(out);
//			    		} catch (UnsupportedEncodingException e) {
//							e.printStackTrace();
//						} catch (IOException e) {
//							e.printStackTrace();
//						} finally {
//							System.out.println("Closing inputstream");
//			    			try {
//								inputStream.close();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//			    		}
//			    	}
//			    }
//			).start();
			
			ContentHandler handler = new ToXMLContentHandler(System.out, 
					metadata.get(Metadata.CONTENT_ENCODING)); 
			
			Parser parser = new AutoDetectParser();
			parser.parse(stream, handler, metadata, new ParseContext());
			
//			System.out.println("Closing outputstream");
//			outputStream.flush();
//			outputStream.close();
			
//			System.out.println(conn.getHeaderFields());
//			System.out.println(metadata);
//			System.out.println(handler.toString());
//			stream.close();
//			out.close();
			
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
	
//	private static class StreamingQueueWriter {
//		
//	}
}
