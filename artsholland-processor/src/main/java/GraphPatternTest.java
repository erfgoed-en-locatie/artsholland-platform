import org.waag.ah.enrich.EnrichUtils;
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.rdf.EnricherConfig;


public class GraphPatternTest {

	public static void main(String[] args) throws ConnectionException {
		
		// Add Geonames enricher.
		EnricherConfig config = new EnricherConfig();
		config.setObjectUri("http://purl.org/artsholland/1.0/Venue");
		config.addIncludeUri(
				"http://www.w3.org/2003/01/geo/wgs84_pos#lat",
				"http://www.w3.org/2003/01/geo/wgs84_pos#long");
		config.addExcludeUri(
				"http://www.geonames.org/ontology#Feature");
		
		String query = EnrichUtils.getObjectQuery(config, 10);
		System.out.println(query);
	}
}
