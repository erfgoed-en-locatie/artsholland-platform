package org.waag.ah.tinkerpop;

import java.util.ArrayList;
import java.util.List;

import org.geonames.FeatureClass;
import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.WebService;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.util.GraphUtil;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.rdf.NamedGraph;

public class GeoNamesEnricher extends AbstractEnricher { 

	public GeoNamesEnricher(EnricherConfig config) {
		super(config);
		WebService.setUserName("artsholland");
		WebService.setDefaultStyle(Style.FULL);
	}

	@Override
	public List<Statement> enrich(EnricherConfig config, NamedGraph graph) {
		List<Statement> statements = new ArrayList<Statement>();
		
		ValueFactory vf = graph.getValueFactory();
		
		try {
			double latitude = GraphUtil.getUniqueObjectLiteral(graph, null, vf.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#lat")).doubleValue();
			double longitude = GraphUtil.getUniqueObjectLiteral(graph, null, vf.createURI("http://www.w3.org/2003/01/geo/wgs84_pos#long")).doubleValue();

			List<Toponym> searchResult = WebService.findNearby(latitude, longitude, FeatureClass.A, new String[]{"ADM2"});
//			List<Toponym> searchResult = WebService.findNearbyPlaceName(latitude, longitude, 50, 10);
			
			for (Toponym toponym : searchResult) {
//				System.out.println(toponym.toString());
				statements.add(
						vf.createStatement(graph.getGraphUri(), 
						vf.createURI("http://www.geonames.org/ontology#Feature"), 
						vf.createURI("http://sws.geonames.org/"+toponym.getGeoNameId())));
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return statements;
	}
}
