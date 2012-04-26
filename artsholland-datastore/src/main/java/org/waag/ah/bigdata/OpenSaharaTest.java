package org.waag.ah.bigdata;

import java.util.Arrays;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.commons.dbcp.BasicDataSource;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.waag.ah.RepositoryConnectionFactory;

import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.useekm.indexing.IndexingSail;
import com.useekm.indexing.IndexingSailConnection;
import com.useekm.indexing.postgis.PostgisIndexMatcher;
import com.useekm.indexing.postgis.PostgisIndexerSettings;

@Singleton
@DependsOn("BigdataConnectionService")
public class OpenSaharaTest {
		
	@EJB(mappedName="java:app/datastore/BigdataConnectionService")
	private RepositoryConnectionFactory cf;
	
	public BigdataSail getSail()  {
		BigdataSailRepository repo = (BigdataSailRepository) cf.getSail();
		return repo.getSail();		
	}
	
	public SailRepositoryConnection getConnection() throws SailException, RepositoryException {
		
		Sail sail = getSail();		
		
		 //Initialize the datasource to be used for connections to Postgres:
		 BasicDataSource pgDatasource = new BasicDataSource();
		 pgDatasource.setDriverClassName("org.postgresql.Driver");
		 pgDatasource.setUrl("jdbc:postgresql://localhost:5432/artsholland-geo");
		 pgDatasource.setUsername("artsholland");
		 pgDatasource.setPassword("artsholland");
		 	
		//Initialize the settings for the Postgis Indexer:
		PostgisIndexerSettings settings = new PostgisIndexerSettings();
		
		settings.setDataSource(pgDatasource);
		
		PostgisIndexMatcher wktMatcher = new PostgisIndexMatcher();
		wktMatcher.setPredicate("http://purl.org/artsholland/1.0/geo"); 
													   
		//		//specify statements that should be indexed:
//		PostgisIndexMatcher matcher1 = new PostgisIndexMatcher();
//		matcher1.setPredicate("http://example.org/geometry"); //to index statements with this predicate, adapt to your needs
		
//		PostgisIndexMatcher matcher2 = new PostgisIndexMatcher();
//		matcher2.setPredicate("http://www.w3.org/2000/01/rdf-schema#label");
//		matcher2.setSearchConfig("simple");
		
		// add matchers for each predicate for wich statements need to be indexed:
		settings.setMatchers(Arrays.asList(new PostgisIndexMatcher[] { wktMatcher }));
		
		//Initialize the IndexingSail that wraps your BigdataSail:
		settings.initialize(true);
		IndexingSail idxSail = new IndexingSail(sail, settings);
		
				
		//Wrap in a SailRepository:
		SailRepository repository = new SailRepository(idxSail);
		//repository.initialize();
		
	
		
		IndexingSailConnection koek = idxSail.getConnection();
		koek.reindex();
		
		return repository.getConnection();
		
	}
}
