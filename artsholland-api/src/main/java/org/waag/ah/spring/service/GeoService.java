package org.waag.ah.spring.service;

import java.util.ArrayList;

import javax.ejb.EJB;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.SailException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.waag.ah.bigdata.OpenSaharaConnectionService;
import org.waag.ah.rest.model.AHRDFNamespaces;

import com.useekm.indexing.exception.IndexException;

@Service("geoService")
public class GeoService implements InitializingBean {

	@EJB(mappedName="java:app/datastore/OpenSaharaConnectionService")
	private OpenSaharaConnectionService openSahara;
	private RepositoryConnection conn;
		
	@Override
	public void afterPropertiesSet() throws Exception {
			conn = openSahara.getConnection();
	}
	
	public double metersToDegrees(double meters) {
		return meters / (Math.PI/180) / 6378137;
	}
	
	public String getVis() throws QueryEvaluationException, MalformedQueryException, RepositoryException {
		// TODO Auto-generated method stub
		String query = 
				AHRDFNamespaces.getSPARQLPrefix() +
				"PREFIX search: <http://rdf.opensahara.com/search#> \n"+
				"SELECT DISTINCT ?venue ?city ?geometry \n" +
				"WHERE { \n" +
						"?venue a ah:Venue . \n" +
						"?venue vcard:locality ?city ." + 
						"?venue <http://purl.org/artsholland/1.0/wkt> ?geometry . \n" +
						"FILTER(search:distance(?geometry, \"POINT(52.3617706 4.8913312)\"^^<http://rdf.opensahara.com/type/geo/wkt>) < kilometer)  \n" +
				"} ORDER BY ?venue ?city ?geometry ";
		
		query = query.replaceFirst("kilometer", Double.toString(metersToDegrees(760)));
		//673
		//double distance(geometry, geometry)

		String query2 = 
				AHRDFNamespaces.getSPARQLPrefix() +
				"PREFIX search: <http://rdf.opensahara.com/search#> \n" +
					"SELECT DISTINCT ?result ?description WHERE { \n" +
			  	"?result dc:description ?description . \n" +
			  	"FILTER(search:text(?description, \"Engeland\")) \n" +
				"}";
		
		TupleQuery koek = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult hond = koek.evaluate();
		
		ArrayList<String> results = new ArrayList<String>();
		BindingSet chips = null;
		while (hond.hasNext()) {			
			chips = hond.next();
			results.add(chips.toString() + "<br />");
		}
		
		return results.toString();	
		
	}

	public void reindex() {
		try {
			openSahara.reindex();
		} catch (IndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
