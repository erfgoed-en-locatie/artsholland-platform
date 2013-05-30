package org.waag.rdf.sesame;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.NotifyingSail;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.service.RepositoryConnectionFactory;

import com.useekm.indexing.IndexingSail;

@Startup
@Singleton
public class RepositoryConnectionService implements RepositoryConnectionFactory, Closeable {
	 private static final Logger logger = LoggerFactory.getLogger(RepositoryConnectionService.class);

	private SailRepository repository;
//	private RepositoryConnection connection;
	private PlatformConfig config;

//	private PropertiesConfiguration properties;
	
	/**
	 * Initialize repository and SAIL stack.
	 * 
	 * @throws ConfigurationException
	 * @throws RepositoryException
	 * 
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @throws IOException 
	 * @since Oct 8, 2012
	 */
	@PostConstruct
	private void init() throws ConfigurationException, RepositoryException {
		config = PlatformConfigHelper.getConfig();
		try {
			// TODO: Maybe introduce something like a RepositoryConfig...
			String repoClassName = config.getString("repository."+config.getString("platform.repository")+".class");
			logger.info("Using repository class: "+repoClassName);
			
			Class<?> clazz = Class.forName(repoClassName);
			
			if (SailFactory.class.isAssignableFrom(clazz)) {
				SailFactory provider = (SailFactory) clazz.newInstance();
				Sail sail = provider.getSail();
	
				// Try to add inferencing...
				if (sail instanceof NotifyingSail) {
					logger.info("Using full inferencer for native Sail implementation...");
					sail = new ForwardChainingRDFSInferencer((NotifyingSail) sail);
				}
//				new DirectTypeHierarchyInferencer(
//						   new ForwardChainingRDFSInferencer(
//								   (NotifyingSail) sail));
//				} else {
//					logger.info("Using simple inferencer for third-party Sail implementation...");
//					sail = new SimpleTypeInferencingSail(sail);
//				}
				
				// Add PostGIS indexer & fulltext search to SAIL stack.
				// TODO: Update PostgreSQL table latout. 
				sail = new IndexingSail(sail, PostgisIndexerSettingsGenerator.generateSettings());

				// Optimize performance by allowing concurrent read/writes.
				// Do we want a (uSeekm) Spring dependency here? Additionally, the
				// PipelineSailConnection stores all statements in memory before
				// committing.
//				SmartSailWrapper smartsail = new SmartSailWrapper(sail);
//				smartsail.setConnectionFactory(connectionFactory)
//				sail = new PipelineSail(sail);
//				new SmartSailWrapper();
//				repository = new BigdataSailRepository((BigdataSail) sail);
				repository = new SailRepository(sail);
			}
			
//			config = PlatformConfigHelper.getConfig();
			
//			Properties properties = ConfigurationConverter.getProperties(loadProperties());
//			Sail sail = new BigdataSail(properties);		
//			IndexingSail idxSail = new IndexingSail(sail, PostgisIndexerSettingsGenerator.generateSettings());

//			SailRepositoryProvider provider = new SailRepositoryProvider();
//			provider.setRepository(repository);

			// Create and initialize repository instance.
			
//			if (!repository.isInitialized()) {
			repository.initialize();
			
			// Add our vocabulary.
			SailRepositoryConnection connection = repository.getConnection();
			connection.setAutoCommit(false);
			InputStream vocabulary = getClass().getResourceAsStream("/org/waag/ah/rdf/schema/artsholland.rdf");
			connection.add(vocabulary, config.getString("platform.baseUri"), RDFFormat.RDFXML);
			connection.commit();
			connection.close();
			
//			}
		} catch (Exception e) {
//			close();
			throw new RepositoryException(e);
		}
	}
	
//	private Configuration loadProperties() throws ConfigurationException {
//		properties = new PropertiesConfiguration("bigdata.properties");
//		properties.setProperty(Options.FILE, config.getProperty("repository.bigdata.datastore"));
//		return properties;
//	}
	
	/**
	 * Close repository (J2EE).
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Oct 9, 2012
	 */
	@PreDestroy
	private void preDestroy() {
		try {
			close();
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}
	
	/**
	 * Close repository.
	 * 
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Oct 9, 2012
	 */
	public void close() throws IOException {
		try {
//			connection.close();
			repository.shutDown();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/*
	 * Return repository connection with autocommit enabled.
	 * 
	 * @author Raoul Wissink <raoul@waag.org>
	 * @see org.waag.ah.RepositoryConnectionFactory#getConnection()
	 */
	@Override
	public RepositoryConnection getConnection()
			throws RepositoryException {
		return getConnection(false);
	}

	/*
	 * Return repository connection.
	 * 
	 * @author Raoul Wissink <raoul@waag.org>
	 * @see org.waag.ah.RepositoryConnectionFactory#getConnection(boolean)
	 */
	@Override
	public RepositoryConnection getConnection(
			boolean autoCommit) throws RepositoryException {
//		logger.debug("Trying to open connection...");
//		RepositoryConnection connection = repository.getConnection();
//		logger.debug("Opened connection: "+connection);
//		connection.setAutoCommit(autoCommit);
		// TDOD: Static connection only needed for BigData, as getConnection()
		//       blocks after the first call. FIX ASAP.
//		return connection;
//		return repository.getReadOnlyConnection();
		return getReadWriteConnection();
	}
	
	@Override
	public RepositoryConnection getReadWriteConnection() throws RepositoryException {
		SailRepositoryConnection conn = repository.getConnection();
		conn.setAutoCommit(false);
		return conn;
//		return getConnection();
	}
}
