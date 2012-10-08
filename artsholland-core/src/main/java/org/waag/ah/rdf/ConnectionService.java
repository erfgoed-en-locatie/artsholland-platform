package org.waag.ah.rdf;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.Sail;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.RepositoryFactory;

@Singleton
public class ConnectionService implements RepositoryConnectionFactory {
//	private static final Logger logger = LoggerFactory.getLogger(ConnectionService.class);

	private PlatformConfig config;
	private SailRepository repository;
//	private SailRepositoryConnection connection; 
	
	@PostConstruct
	public void init() throws ConfigurationException, RepositoryException {
		config = PlatformConfigHelper.getConfig(); 
		try {
			Sail sail = ((RepositoryFactory) Class.forName(config.getString("platform.repository")).newInstance()).getSail();
//			PostgisIndexerSettings settings = PostgisIndexerSettingsGenerator.generateSettings();			
//			IndexingSail idxSail = new IndexingSail(sail, settings);
//			repository = new SailRepository(idxSail);
			
//			Repository myRepository = new SailRepository(
//                    new ForwardChainingRDFSInferencer(
//                    new MemoryStore()));
			
//			repository = new SailRepository(
//					new DirectTypeHierarchyInferencer(
//					new ForwardChainingRDFSInferencer((NotifyingSail) sail)));
//			repository.initialize();			
			
//			ForwardChainingRDFSInferencer inferencer = new ForwardChainingRDFSInferencer((NotifyingSail) sail);
			
			repository = new SailRepository(sail);
			repository.initialize();			
			
//			System.out.println("SCHEMA URL: "+Thread.currentThread().getContextClassLoader().getResource("/org/waag/ah/rdf/schema/artsholland.rdf"));
			
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	/*
	 * Return repository connection with autocommit enabled.
	 * 
	 * @author Raoul Wissink <raoul@waag.org>
	 * @see org.waag.ah.RepositoryConnectionFactory#getConnection()
	 */
	@Override
	public SailRepositoryConnection getConnection() throws RepositoryException {
		return getConnection(true);
	}

	/*
	 * Return repository connection with autocommit disabled.
	 * 
	 * @author Raoul Wissink <raoul@waag.org>
	 * @see org.waag.ah.RepositoryConnectionFactory#getConnection(boolean)
	 */
	@Override
	public synchronized SailRepositoryConnection getConnection(boolean autoCommit) throws RepositoryException {
		SailRepositoryConnection connection = repository.getConnection();
		connection.setAutoCommit(autoCommit);			
		return connection;
	}
}
