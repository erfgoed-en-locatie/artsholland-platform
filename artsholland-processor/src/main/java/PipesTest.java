import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.waag.ah.tinkerpop.pipes.FetchURLPipe;
import org.waag.ah.tinkerpop.pipes.TikaParserPipe;
import org.waag.ah.tinkerpop.pipes.StreamReaderPipe;

import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.util.Pipeline;


public class PipesTest {

	public static void main(String[] args) throws MalformedURLException {

		List<URL> resources = new ArrayList<URL>();
		resources.add(new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=0&createdto=2012-04-29T13:44:00.007Z"));
		resources.add(new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=500&createdto=2012-04-29T13:44:00.007Z"));

		Pipe<URL, InputStream> fetchPipe = new FetchURLPipe();
		Pipe<InputStream, InputStream> streamParserPipe = new TikaParserPipe(); 
		Pipe<InputStream, String> streamReaderPipe = new StreamReaderPipe();
		
		Pipeline<URL, String> pipeline = new Pipeline<URL, String>(fetchPipe, streamParserPipe, streamReaderPipe);
		pipeline.setStarts(resources);
		
		while(pipeline.hasNext()) {
			System.out.println(pipeline.next());
		}		
	}
}
