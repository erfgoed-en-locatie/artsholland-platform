package org.waag.ah.spring.service;

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
	
	public String getVis() throws QueryEvaluationException, MalformedQueryException, RepositoryException {
		// TODO Auto-generated method stub
		String query = 
				"SELECT DISTINCT ?resource ?value" +
				"WHERE { ?resource <http://purl.org/artsholland/1.0/wkt> ?value }" +
				"ORDER BY ?resource ?value";
		
		TupleQuery koek = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult hond = koek.evaluate();
		
		while (hond.hasNext()) {
			BindingSet chips = hond.next();
		}
		
		return "Hondje";	
		
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
