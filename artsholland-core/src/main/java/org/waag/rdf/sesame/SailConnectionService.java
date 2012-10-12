package org.waag.rdf.sesame;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.NotifyingSail;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.DirectTypeHierarchyInferencer;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;

import com.useekm.inference.SimpleTypeInferencingSail;
import com.useekm.pipeline.PipelineSail;

@Singleton
public class SailConnectionService implements SailConnectionFactory, Closeable {
	 private static final Logger logger = LoggerFactory.getLogger(SailConnectionService.class);

	private SailRepository repository;

	/**
	 * Close repository.
	 * 
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Oct 9, 2012
	 */
	public void close() throws IOException {
		try {
			repository.shutDown();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Close repository (J2EE).
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Oct 9, 2012
	 */
	@PreDestroy
	private void preDestroy() {
		try {
			repository.shutDown();
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}
	
	/**
	 * Initialize repository and SAIL stack.
	 * 
	 * @throws ConfigurationException
	 * @throws RepositoryException
	 * 
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @since Oct 8, 2012
	 */
	private void init() throws ConfigurationException, RepositoryException {
		PlatformConfig config = PlatformConfigHelper.getConfig();
		try {
			// TODO: Maybe introduce something like a RepositoryConfig...
			String clazz = config.getString("repository."
					+ config.getString("platform.repository") + ".class");
			Sail sail = ((SailFactory) Class.forName(clazz).newInstance())
					.getSail();

			// Try to add inferencing...
			if (sail instanceof NotifyingSail) {
				sail = new DirectTypeHierarchyInferencer(
					   new ForwardChainingRDFSInferencer(
					   (NotifyingSail) sail));
			} else {
				sail = new SimpleTypeInferencingSail(sail);
			}
			
			// Add PostGIS indexer & fulltext search to SAIL stack.
			// TODO: Update PostgreSQL table latout. 
//			sail = new IndexingSail(sail,
//					PostgisIndexerSettingsGenerator.generateSettings());

			// Optimize performance by allowing concurrent read/writes.
			// Do we want a (uSeekm) Spring dependency here? Additionally, the
			// PipelineSailConnection stores all statements in memory before
			// committing.
//			sail = new SmartSailWrapper(sail);
//			sail = new PipelineSail(sail);

			// Create and initialize repository instance.
			repository = new SailRepository(sail);
			repository.initialize();
			
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
	public SailRepositoryConnection getConnection()
			throws RepositoryException {
		return getConnection(true);
	}

	/*
	 * Return repository connection.
	 * 
	 * @author Raoul Wissink <raoul@waag.org>
	 * @see org.waag.ah.RepositoryConnectionFactory#getConnection(boolean)
	 */
	@Override
	public synchronized SailRepositoryConnection getConnection(
			boolean autoCommit) throws RepositoryException {
		if (repository == null) {
			try {
				init();
				logger.debug("Opended repository connection: "+repository);
			} catch (Exception e) {
				throw new RepositoryException(e);
			}
		}	
		SailRepositoryConnection connection = repository.getConnection();
		logger.debug("Opened connection: "+connection);
		connection.setAutoCommit(autoCommit);
		return connection;
	}
}
