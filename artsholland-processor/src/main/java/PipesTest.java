import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openrdf.model.Statement;
import org.waag.ah.tinkerpop.EnricherPipeline;
import org.waag.ah.tinkerpop.ParserPipeline;

import com.tinkerpop.pipes.util.Pipeline;

public class PipesTest {

	public static void main(String[] args) throws MalformedURLException {

		List<URL> resources = new ArrayList<URL>(Arrays.asList(
//			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=10&start=0&createdto=2012-04-29T13:44:00.007Z")
//			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=0&createdto=2012-04-29T13:44:00.007Z"),
			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=500&createdto=2012-04-29T13:44:00.007Z")
		));
		
		// Assemble pipeline.
		Pipeline<URL, List<Statement>> pipeline = new Pipeline<URL, List<Statement>>(
				new ParserPipeline(),
				new EnricherPipeline(),
				new ToStringPipe()
				);
		pipeline.setStarts(resources);
		
		while(pipeline.hasNext()) {
			System.out.println(pipeline.next());
		}		
	}
}
