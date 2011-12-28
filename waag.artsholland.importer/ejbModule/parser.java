import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.waag.artsholland.parser.sax.StreamingContentHandler;
import org.xml.sax.SAXException;


public class parser {

	/**
	 * @param args
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Dec 18, 2011
	 */
	public static void main(String[] args) {
		URLConnection conn = null;
		try {
//			FileWriter fstream = new FileWriter("out.txt");
//			BufferedWriter out = new BufferedWriter(fstream);
//			BufferedWriter out = new BufferedWriter(System.out);
			
//			String sourceURL = "http://127.0.0.1/ah/nub/events.xml";
//			String sourceURL = "http://127.0.0.1/ah/nub/amsterdam.xml";
//			String sourceURL = "http://test.publisher.uitburo.nl/agenda/search.do?key=e17c6b21b6852e1ab43abdfdf034f752&locationText=Amsterdam&start=0&rows=500";
			String sourceURL = "http://127.0.0.1/ah/dos/2011_jaarboek_1816.xls";
			
			conn =  new URL(sourceURL).openConnection();

//			Map<String, List<String>> header = conn.getHeaderFields();

//			StreamingContentHandler handler = new StreamingContentHandler(out); 
			StreamingContentHandler handler = new StreamingContentHandler(System.out); 
			InputStream stream = conn.getInputStream();
			Metadata metadata = new Metadata();
			metadata.set(Metadata.RESOURCE_NAME_KEY, sourceURL);
			Parser parser = new AutoDetectParser();
			ParseContext parseContext = new ParseContext();
			
			parser.parse(stream, handler, metadata, parseContext);
			
//			System.out.println(metadata);
//			System.out.println(handler.toString());
			stream.close();
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
}
