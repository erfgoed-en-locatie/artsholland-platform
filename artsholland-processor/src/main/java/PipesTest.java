//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.openrdf.model.Statement;
//import org.openrdf.repository.sail.SailRepositoryConnection;
//import org.waag.ah.bigdata.BigdataConnectionService;
//import org.waag.ah.exception.ConnectionException;
//import org.waag.ah.tinkerpop.pipe.EnricherPipeline;
//import org.waag.ah.tinkerpop.pipe.ParserPipeline;
//import org.waag.ah.tinkerpop.pipe.ProcessorPipeline;
//
//import com.tinkerpop.pipes.util.Pipeline;
//
//public class PipesTest {
//
//	public static void main(String[] args) throws MalformedURLException, ConnectionException {
//
//		List<URL> resources = new ArrayList<URL>(Arrays.asList(
//			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=10&start=0&createdto=2012-05-30T12:25:00.001Z&filter=resource:locations")
////			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=10&start=0&createdto=2012-04-29T13:44:00.007Z")
////			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=0&createdto=2012-04-29T13:44:00.007Z"),
////			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=500&createdto=2012-04-29T13:44:00.007Z"),
////			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=1000&createdto=2012-04-29T13:44:00.007Z"),
////			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=1500&createdto=2012-04-29T13:44:00.007Z"),
////			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=2000&createdto=2012-04-29T13:44:00.007Z"),
////			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=2500&createdto=2012-04-29T13:44:00.007Z"),
////			new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=3000&createdto=2012-04-29T13:44:00.007Z")
//		));
//		
//		// Assemble pipeline.
//		Pipeline<URL, List<Statement>> pipeline = new Pipeline<URL, List<Statement>>(
//				new ParserPipeline(),
//				new ProcessorPipeline(),
//				new EnricherPipeline(),
//				new ToStringPipe()
//				);
//		pipeline.setStarts(resources);
//		
//		BigdataConnectionService datastore = new BigdataConnectionService();
//		SailRepositoryConnection conn = datastore.getConnection();
//		
//		while(pipeline.hasNext()) {
//			System.out.println(pipeline.next());
//		}		
//	}
//}
