import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;


public class parser {
	public static void main(String[] args) {
		
		String[] sourceURLs = {
			//"http://waxworks.nl/events.xml",
			//"http://127.0.0.1/ah/nub/events.xml",
			//"http://127.0.0.1/ah/nub/amsterdam.xml",
			//"http://test.publisher.uitburo.nl/agenda/search.do?key=e17c6b21b6852e1ab43abdfdf034f752&locationText=Amsterdam&start=0&rows=500",
			//"http://127.0.0.1/ah/dos/2011_jaarboek_1816.xls",
				
			"http://localhost/ah/nub/v4/event.xml",
			"http://localhost/ah/nub/v4/production.xml",
			"http://localhost/ah/nub/v4/location.xml"
		};
							
		try {
			for (String sourceURL: sourceURLs) {
				System.out.println("\n\n============================= " + sourceURL + " ============================\n");
				URLConnection conn = new URL(sourceURL).openConnection();
				InputStream stream = conn.getInputStream();
				
				final Metadata metadata = new Metadata();
				metadata.set(Metadata.RESOURCE_NAME_KEY, sourceURL);
				
				InputStreamReader r = new InputStreamReader(stream);
				metadata.set(Metadata.CONTENT_ENCODING, r.getEncoding());

				ContentHandler handler = new ToXMLContentHandler(System.out, 
						metadata.get(Metadata.CONTENT_ENCODING)); 
				
				Parser parser = new AutoDetectParser();
				parser.parse(stream, handler, metadata, new ParseContext());	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
