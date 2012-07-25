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

import com.restfb.DefaultFacebookClient;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
import com.restfb.types.Location;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.Contact;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class SocialMediaEnricher extends AbstractEnricher {

	protected static class FqlPage {
		@Facebook
		String page_id;
		
		@Facebook
		String name;
		
		@Facebook
		String website;
		
		@Facebook
		String page_url;
	
		@Facebook
		String fan_count;
		
		@Facebook
		Location location;
	}	
	
	private PlatformConfig platformConfig;
	
	private FoursquareApi foursquareApi;
	private FacebookClient facebookClient;
	
	public SocialMediaEnricher(EnricherConfig enricherConfig)
			throws ConfigurationException {
		super(enricherConfig);
		this.platformConfig = PlatformConfigHelper.getConfig();
		
		// Foursquare
		String clientId = platformConfig.getString("foursquare.api.clientId");
		String clientSecret = platformConfig.getString("foursquare.api.clientSecret");		
		this.foursquareApi = new FoursquareApi(clientId, clientSecret, "http://dev.artsholland.com");
		
		// Facebook
		//String accessToken = platformConfig.getString("facebook.api.accessToken");
		//facebookClient = new DefaultFacebookClient(accessToken);
		facebookClient = new DefaultFacebookClient();
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

			getFoursquareStatements(graph, statements, vf, classUri, homepageStr, lat, lon);
		} catch (FoursquareApiException e) {
			e.printStackTrace();
		} catch (GraphUtilException e) {
			e.printStackTrace();
		}

		return statements;
	}

	protected void getFacebookStatements(NamedGraph graph, List<Statement> statements,
			ValueFactory vf, String classUri, String homepageStr, float lat, float lon, String name, String city) {		
		
		String query = "SELECT page_id, name, website, page_url, fan_count, location FROM page WHERE name = \"%s\"";		
		List<FqlPage> pages = facebookClient.executeQuery(String.format(query, name), FqlPage.class);

		if (pages.size() >= 1) {
			FqlPage page = pages.get(0);
			String facebookCity = page.location.getCity();
			if (facebookCity != null && facebookCity.equals(city)) {			
				String pageUrl = page.page_url;
				String pageId = page.page_id;			
				statements.add(vf.createStatement(graph.getGraphUri(),
						vf.createURI(classUri + "facebookPageId"), 
						vf.createLiteral(pageId)));
				statements.add(vf.createStatement(graph.getGraphUri(),
						vf.createURI(classUri + "facebookUrl"), 
						vf.createURI(pageUrl)));
			}
		}
	}
	
	protected void getFoursquareStatements(NamedGraph graph, List<Statement> statements,
			ValueFactory vf, String classUri, String homepageStr, float lat, float lon)
			throws FoursquareApiException {
		
		Result<VenuesSearchResult> searchResult = foursquareApi.venuesSearch(
				Float.toString(lat) + "," + Float.toString(lon), null, null, null,
				null, null, null, null, homepageStr, null, null);
		
		if (searchResult.getMeta().getCode() == 200) {
			// if query was ok we can finally we do something with the data
			for (CompactVenue venue : searchResult.getResult().getVenues()) {
				
				String name = venue.getName();
				String city = venue.getLocation().getCity();
				getFacebookStatements(graph, statements, vf, classUri, homepageStr, lat, lon, name, city);
				
				String foursquareId = venue.getId();
				Contact contact = venue.getContact();
				String twitterId = contact.getTwitter();
				
				if (foursquareId != null) {
					statements.add(vf.createStatement(graph.getGraphUri(),
							vf.createURI(classUri + "foursquareId"), 
							vf.createLiteral(foursquareId)));
					
					statements.add(vf.createStatement(graph.getGraphUri(),
							vf.createURI(classUri + "foursquareUrl"), 
							vf.createURI(getFoursquareUrl(foursquareId))));
				}

				if (twitterId != null) {
					statements.add(vf.createStatement(graph.getGraphUri(),
							vf.createURI(classUri + "twitterId"),
							vf.createLiteral(twitterId)));
					
					statements.add(vf.createStatement(graph.getGraphUri(),
							vf.createURI(classUri + "twitterUrl"),
							vf.createURI(getTwitterUrl(twitterId))));
				}
			}
		}
	}

	private String getTwitterUrl(String twitterId) {
		return "https://twitter.com/" + twitterId;
	}

	private String getFoursquareUrl(String foursquareId) {
		return "https://foursquare.com/v/" + foursquareId;
	}
	
}
