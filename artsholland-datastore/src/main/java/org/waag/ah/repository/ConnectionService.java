package org.waag.ah.repository;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.Sail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.RepositoryFactory;
import org.waag.ah.useekm.PostgisIndexerSettingsGenerator;

import com.useekm.indexing.IndexingSail;
import com.useekm.indexing.postgis.PostgisIndexerSettings;

@Singleton
public class ConnectionService implements RepositoryConnectionFactory {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionService.class);

	private PlatformConfig config;
	private SailRepository repository;
	private SailRepositoryConnection connection; 
	
	@PostConstruct
	public void init() throws ConfigurationException, RepositoryException {
		config = PlatformConfigHelper.getConfig(); 
		try {
			Sail sail = ((RepositoryFactory) Class.forName(config.getString("platform.repository")).newInstance()).getSail();
			PostgisIndexerSettings settings = PostgisIndexerSettingsGenerator.generateSettings();			
			IndexingSail idxSail = new IndexingSail(sail, settings);
			repository = new SailRepository(idxSail);
			repository.initialize();			
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}
	
	@Override
	public synchronized SailRepositoryConnection getConnection() throws RepositoryException {
		logger.info("Trying to get new connection");
		connection = repository.getConnection();
		connection.setAutoCommit(false);			
		logger.info("Returning new connection");
		return connection;
	}
}
