package org.waag.ah.tinkerpop.enrich;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.model.Statement;
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
			String homepage = GraphUtil.getUniqueObjectLiteral(graph, null, vf.createURI("http://xmlns.com/foaf/0.1/homepage")).stringValue();

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
