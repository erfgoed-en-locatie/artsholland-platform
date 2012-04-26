package org.waag.ah.bigdata;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.commons.dbcp.BasicDataSource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.waag.ah.RepositoryConnectionFactory;

import com.bigdata.rdf.sail.BigdataSailRepository;
import com.useekm.bigdata.BigdataSail;
import com.useekm.indexing.IndexingSail;
import com.useekm.indexing.IndexingSailConnection;
import com.useekm.indexing.exception.IndexException;
import com.useekm.indexing.postgis.PostgisIndexMatcher;
import com.useekm.indexing.postgis.PostgisIndexerSettings;

@Singleton
@DependsOn("BigdataConnectionService")
public class OpenSaharaConnectionService {

	@EJB(mappedName = "java:app/datastore/BigdataConnectionService")
	private RepositoryConnectionFactory cf;
	
	private IndexingSail idxSail;
	private SailRepository repo;

	@PostConstruct
	public void create() throws RepositoryException {
		
		Sail sail = cf.getSail();

		// Initialize the datasource to be used for connections to Postgres:
		BasicDataSource pgDatasource = new BasicDataSource();
		pgDatasource.setDriverClassName("org.postgresql.Driver");
		pgDatasource.setUrl("jdbc:postgresql://localhost:5432/useekm");
		pgDatasource.setUsername("artsholland");
		pgDatasource.setPassword("artsholland");

		// Initialize the settings for the Postgis Indexer:
		PostgisIndexerSettings settings = new PostgisIndexerSettings();

		settings.setDataSource(pgDatasource);

		PostgisIndexMatcher wktMatcher = new PostgisIndexMatcher();
		wktMatcher.setPredicate("http://purl.org/artsholland/1.0/wkt");
		
		// add matchers for each predicate for wich statements need to be indexed:
		settings.setMatchers(Arrays
				.asList(new PostgisIndexMatcher[] { wktMatcher }));

		// Initialize the IndexingSail that wraps your BigdataSail:
		settings.initialize(true);
		
		idxSail = new IndexingSail(sail, settings);

		// Wrap in a SailRepository:
		repo = new SailRepository(idxSail);
		
	}

	public void reindex() throws SailException, IndexException {
		IndexingSailConnection idxConn = idxSail.getConnection();
		idxConn.reindex();
		idxConn.close();
	}

	public Repository getRepository() {
		return repo;
	}
	
	public RepositoryConnection getConnection () throws RepositoryException {
		return repo.getConnection();
	}

	public IndexingSailConnection getIndexingSailConnection() throws IOException,
			RepositoryException, SailException {		
		IndexingSailConnection idxConn = idxSail.getConnection();		
		return idxConn;
	}	
	
}
