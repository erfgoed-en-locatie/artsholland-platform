package org.waag.ah.repository;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.RepositoryFactory;
import org.waag.ah.useekm.PostgisIndexerSettingsGenerator;

import com.useekm.indexing.IndexingSail;
import com.useekm.indexing.postgis.PostgisIndexerSettings;

@Singleton
public class ConnectionService implements RepositoryConnectionFactory {
	private PlatformConfig config;
	private SailRepository repository;
	private SailRepositoryConnection connection; 
	
	@PostConstruct
	public void init() throws ConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException, SailException, RepositoryException {
		config = PlatformConfigHelper.getConfig(); 

		Sail sail = ((RepositoryFactory) Class.forName(config.getString("platform.repository")).newInstance()).getSail();
		PostgisIndexerSettings settings = PostgisIndexerSettingsGenerator.generateSettings();			
		IndexingSail idxSail = new IndexingSail(sail, settings);
		
		repository = new SailRepository(idxSail);
		repository.initialize();			
	}
	
	@Override
	public synchronized SailRepositoryConnection getConnection() throws RepositoryException {
//		if (connection == null) {
			connection = repository.getConnection();
			connection.setAutoCommit(false);			
//		}
		return connection;
	}
}
