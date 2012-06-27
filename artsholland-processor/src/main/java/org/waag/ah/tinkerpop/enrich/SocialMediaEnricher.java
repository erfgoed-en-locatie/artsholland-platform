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

public class SocialMediaEnricher extends AbstractEnricher { 
	
	public SocialMediaEnricher(EnricherConfig enricherConfig) throws ConfigurationException {
		super(enricherConfig);		
	}

	@Override
	public List<Statement> enrich(EnricherConfig config, NamedGraph graph) {
		List<Statement> statements = new ArrayList<Statement>();
		ValueFactory vf = graph.getValueFactory();
		
		// Facebook, met FQL:
		// SELECT name, website, page_url, fan_count, location FROM page WHERE  name = "Gelredome"
		//
		// http://developers.facebook.com/tools/explorer?fql=SELECT%20name%2C%20website%2C%20page_url%2C%20fan_count%2C%20location%20FROM%20page%20WHERE%20%20name%20%3D%20%22Gelredome%22
		// http://developers.facebook.com/docs/reference/fql/page/
		
		//Foursquare (resultaat bevat ook Twitter-naam)
		//https://developer.foursquare.com/docs/explore#req=venues/search%3Fquery%3Drijksmuseum%26ll%3D52.356,4.825+
		//
		//https://developer.foursquare.com/docs/venues/search
		
		return statements;
	}
}
