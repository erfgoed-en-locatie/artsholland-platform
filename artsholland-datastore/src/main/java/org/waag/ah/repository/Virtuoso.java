package org.waag.ah.repository;

import java.net.URL;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.rdf.sesame.SailFactory;

import virtuoso.sesame2.driver.VirtuosoRepository;

import com.useekm.reposail.RepositorySail;

public class Virtuoso implements SailFactory {
	final static Logger logger = LoggerFactory.getLogger(Virtuoso.class);
	
	private VirtuosoRepository virtuoso;
	private RepositorySail repository;

	@Override
	public synchronized Sail getSail() throws SailException {
		if (repository == null) {
			try {
				virtuoso = new VirtuosoRepository("jdbc:virtuoso://localhost:1111", "dba", "dba", true);
				repository = new RepositorySail(virtuoso);
				
				// Needed for inferencing...
				URL schema = Thread.currentThread().getContextClassLoader().getResource("/org/waag/ah/rdf/schema/artsholland.rdf");
				RepositoryConnection connection = virtuoso.getConnection();
				connection.add(schema, null, RDFFormat.RDFXML);
				virtuoso.createRuleSet("artsholland", "http://purl.org/artsholland/1.0/");
				connection.close();
				
			} catch (Exception e) {
				throw new SailException(e);
			}
		}
		return repository;
	}
	
//	private void updateIndexes() {
//	    java.sql.Connection con = ((VirtuosoRepositoryConnection)virtuoso.getConnection()).getQuadStoreConnection();
//	    try {
//	      java.sql.Statement st = con.createStatement();
//	      st.execute("rdfs_rule_set('"+ruleSetName+"', '"+uriGraphRuleSet+"')");
//	      st.close();
//	    } catch (Exception e) {
//	      throw new RepositoryException(e);
//	    } finally {
//	    	con.close();
//	    }
//	}
}
