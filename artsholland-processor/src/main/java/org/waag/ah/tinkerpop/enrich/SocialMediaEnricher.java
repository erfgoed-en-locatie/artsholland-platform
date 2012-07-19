package org.waag.ah.tinkerpop.enrich;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.util.GraphUtilException;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.rdf.NamedGraph;
import org.waag.ah.tinkerpop.AbstractEnricher;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class SocialMediaEnricher extends AbstractEnricher {

	public SocialMediaEnricher(EnricherConfig enricherConfig)
			throws ConfigurationException {
		super(enricherConfig);
	}

	@Override
	public List<Statement> enrich(EnricherConfig config, NamedGraph graph) {
		List<Statement> statements = new ArrayList<Statement>();
		ValueFactory vf = graph.getValueFactory();

		// Facebook, met FQL:
		// SELECT name, website, page_url, fan_count, location FROM page WHERE name
		// = "Gelredome"
		//
		// http://developers.facebook.com/tools/explorer?fql=SELECT%20name%2C%20website%2C%20page_url%2C%20fan_count%2C%20location%20FROM%20page%20WHERE%20%20name%20%3D%20%22Gelredome%22
		// http://developers.facebook.com/docs/reference/fql/page/

		// Foursquare (resultaat bevat ook Twitter-naam)
		// https://developer.foursquare.com/docs/explore#req=venues/search%3Fquery%3Drijksmuseum%26ll%3D52.356,4.825+
		// https://api.foursquare.com/v2/venues/search?ll=52.35999487589336,4.884281158447266&intent=browse&radius=100&url=http://www.rijksmuseum.nl&oauth_token=RFZKZXJEXSC2E3GPSXZTKMF0EGCPXPMBWXUTT04PZOISRYGV&v=20120703

		//
		// https://developer.foursquare.com/docs/venues/search

		try {
			String latStr = GraphUtil.getOptionalObjectResource(graph, null,
					vf.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#lat"))
					.toString();
			String longStr = GraphUtil.getOptionalObjectResource(graph, null,
					vf.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#long"))
					.toString();
			String homepageStr = GraphUtil.getOptionalObjectResource(graph, null,
					vf.createURI("http://xmlns.com/foaf/0.1/homepage")).toString();

			FoursquareApi foursquareApi = new FoursquareApi(
					"IPA5HMU0XWTH1CTSTFBSZZX3MYEEANCG3LH4YCIRAMVLZBI4",
					"5FXJKYR4LU4IST0XWG0TJXPKVJ1SKNV5TJOAJC32N4DQ3RMZ",
					"http://dev.artsholland.com");

			Result<VenuesSearchResult> result = foursquareApi.venuesSearch(
					latStr + "," + longStr, null, null, null, null, null,
					null, null, homepageStr, null, null);
			if (result.getMeta().getCode() == 200) {
				// if query was ok we can finally we do something with the data
				for (CompactVenue venue : result.getResult().getVenues()) {
					// TODO: Do something we the data
					System.out.println(venue.getName());
				}
			} else {
				// TODO: Proper error handling
				System.out.println("Error occured: ");
				System.out.println("  code: " + result.getMeta().getCode());
				System.out.println("  type: " + result.getMeta().getErrorType());
				System.out.println("  detail: " + result.getMeta().getErrorDetail());
			}
		} catch (FoursquareApiException e) {
			e.printStackTrace();
		} catch (GraphUtilException e) {
			e.printStackTrace();
		}

		return statements;
	}
}
