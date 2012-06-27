package org.waag.ah.tinkerpop.enrich;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.util.GraphUtil;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.rdf.NamedGraph;
import org.waag.ah.tinkerpop.AbstractEnricher;

public class DBpediaEnricher extends AbstractEnricher { 
	
	public DBpediaEnricher(EnricherConfig enricherConfig) throws ConfigurationException {
		super(enricherConfig);		
	}

	@Override
	public List<Statement> enrich(EnricherConfig config, NamedGraph graph) {
		List<Statement> statements = new ArrayList<Statement>();
		ValueFactory vf = graph.getValueFactory();
		
		try {
			
			//Facebook
			//SELECT name, website, page_url, fan_count, location FROM page WHERE  name = "Gelredome"			
			//Foursquare (ook twitter-naam)
			//https://developer.foursquare.com/docs/explore#req=venues/search%3Fquery%3Drijksmuseum%26ll%3D52.356,4.825+
			
			Resource vio = GraphUtil.getOptionalObjectResource(graph, null, vf.createURI("http://xmlns.com/foaf/0.1/homepage"));
			
			//String homepage = GraphUtil.getUniqueObjectLiteral(graph, null, vf.createURI("http://xmlns.com/foaf/0.1/homepage")).stringValue();

			String sparql = "SELECT ?resource WHERE { ?resource foaf:homepage <http://www.rijksmuseum.nl/> .	}";
			
			// TODO: Should use federated query!!!!
//			SELECT ?resource WHERE {
//				SERVICE <http://dbpedia.org/sparql> {
//					?resource foaf:homepage <http://www.rijksmuseum.nl/> .	
//				}  
//			}
			
			//DPpediaService.runSPARQL(sparql);
	
//				statements.add(
//						vf.createStatement(graph.getGraphUri(), 
//						vf.createURI("http://www.geonames.org/ontology#Feature"), 
//						vf.createURI("http://sws.geonames.org/"+toponym.getGeoNameId())));
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return statements;
	}
}
