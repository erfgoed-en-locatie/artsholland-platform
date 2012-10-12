package org.waag.ah.enricher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.tinkerpop.AbstractEnricher;
import org.waag.rdf.sesame.EnricherConfig;
import org.waag.rdf.sesame.NamedGraph;

public class DBpediaEnricher extends AbstractEnricher { 
		
	private PlatformConfig platformConfig;
	
	public DBpediaEnricher(EnricherConfig enricherConfig) throws ConfigurationException {
		super(enricherConfig);	
		this.platformConfig = PlatformConfigHelper.getConfig();
	}

	@Override
	public List<Statement> enrich(EnricherConfig config, NamedGraph graph) {
		List<Statement> statements = new ArrayList<Statement>();
		ValueFactory vf = graph.getValueFactory();
		RepositoryConnection conn = config.getConnection();
		try {
			
			String classUri = platformConfig.getString("platform.classUri");						
			String homepageStr = GraphUtil.getOptionalObjectResource(graph, null, vf.createURI("http://xmlns.com/foaf/0.1/homepage")).toString();
			
			String sparql = 
					"SELECT ?dbpedia WHERE { " +
					"		SERVICE <http://dbpedia.org/sparql> { " +
					"			?dbpedia <http://xmlns.com/foaf/0.1/homepage> ?homepage . " +
					"		}" +
					"}";

			List<URI> homepages = new ArrayList<URI>();  
		  Collections.addAll(homepages, vf.createURI(homepageStr), vf.createURI(homepageStr + "/")); 
			
		  for (URI homepage : homepages) {
		  	TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
				query.setBinding("homepage", homepage);
				
				TupleQueryResult queryResult = query.evaluate();
				
				while (queryResult.hasNext()) {
					BindingSet bindingSet = queryResult.next();
					Binding dbpedia = bindingSet.getBinding("dbpedia");
					
					statements.add(
						vf.createStatement(graph.getGraphUri(), 
						vf.createURI(classUri + "dbpedia"), 
						vf.createURI(dbpedia.getValue().stringValue())));				
				}
		  }						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return statements;
	}
}
