package org.waag.ah.tinkerpop.enrich;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.util.GraphUtilException;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.rdf.NamedGraph;
import org.waag.ah.tinkerpop.AbstractEnricher;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.Contact;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class SocialMediaEnricher extends AbstractEnricher {

	private PlatformConfig platformConfig;

	public SocialMediaEnricher(EnricherConfig enricherConfig)
			throws ConfigurationException {
		super(enricherConfig);
		this.platformConfig = PlatformConfigHelper.getConfig();
	}

	@Override
	public List<Statement> enrich(EnricherConfig config, NamedGraph graph) {
		List<Statement> statements = new ArrayList<Statement>();
		ValueFactory vf = graph.getValueFactory();

		String classUri = platformConfig.getString("platform.classUri");

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
			// AHRDFNamespaces.getFullURI("foaf:homepage")
			String homepageStr = GraphUtil.getOptionalObjectResource(graph, null,
					vf.createURI("http://xmlns.com/foaf/0.1/homepage")).toString();

			float lat = GraphUtil.getOptionalObjectLiteral(graph, null,
					vf.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#lat"))
					.floatValue();

			float lon = GraphUtil.getOptionalObjectLiteral(graph, null,
					vf.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#long"))
					.floatValue();

			FoursquareApi foursquareApi = new FoursquareApi(
					"IPA5HMU0XWTH1CTSTFBSZZX3MYEEANCG3LH4YCIRAMVLZBI4",
					"5FXJKYR4LU4IST0XWG0TJXPKVJ1SKNV5TJOAJC32N4DQ3RMZ",
					"http://dev.artsholland.com");

			Result<VenuesSearchResult> searchResult = foursquareApi.venuesSearch(
					Float.toString(lat) + "," + Float.toString(lon), null, null, null,
					null, null, null, null, homepageStr, null, null);
			if (searchResult.getMeta().getCode() == 200) {
				// if query was ok we can finally we do something with the data
				for (CompactVenue venue : searchResult.getResult().getVenues()) {
					String foursquareId = venue.getId();
					Contact contact = venue.getContact();
					String twitterId = contact.getTwitter();
					String facebookId = contact.getFacebook();

					if (foursquareId != null) {
						statements.add(vf.createStatement(graph.getGraphUri(),
								vf.createURI(classUri + "foursquare"),
								vf.createURI(getFoursquareUrl(foursquareId))));
					}

					if (twitterId != null) {
						statements.add(vf.createStatement(graph.getGraphUri(),
								vf.createURI(classUri + "twitter"),
								vf.createURI(getTwitterUrl(twitterId))));
					}

					if (facebookId != null) {
						statements.add(vf.createStatement(graph.getGraphUri(),
								vf.createURI(classUri + "facebook"),
								vf.createURI(getFacebookUrl(facebookId))));
					}

				}
			}
		} catch (FoursquareApiException e) {
			e.printStackTrace();
		} catch (GraphUtilException e) {
			e.printStackTrace();
		}

		return statements;
	}

	private String getFoursquareUrl(String foursquareId) {
		return "http://foursquare.com/venue/" + foursquareId;
	}

	private String getTwitterUrl(String twitterId) {
		return "http://twitter.com/" + twitterId;
	}

	private String getFacebookUrl(String facebookId) {
		return "http://facebook.com/" + facebookId;
	}
}
