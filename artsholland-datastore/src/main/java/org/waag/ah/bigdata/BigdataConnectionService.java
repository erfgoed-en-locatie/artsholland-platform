package org.waag.ah.bigdata;

import javax.ejb.Singleton;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.exception.ConnectionException;
import org.waag.ah.useekm.PostgisIndexerSettingsGenerator;

import com.bigdata.journal.Options;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.useekm.bigdata.BigdataSail;
import com.useekm.indexing.IndexingSail;
import com.useekm.indexing.IndexingSailConnection;
import com.useekm.indexing.exception.IndexException;
import com.useekm.indexing.postgis.PostgisIndexerSettings;

@Singleton
public class BigdataConnectionService implements RepositoryConnectionFactory {
	final static Logger logger = LoggerFactory.getLogger(BigdataConnectionService.class);

	private BigdataSailRepository bigdataRepo;
	private PropertiesConfiguration properties;
	private Sail sail;
	private SailRepository repo; 

	private synchronized void connect() throws ConnectionException {
		
		try {
			if (bigdataRepo == null) {
				bigdataRepo = createRepository(loadProperties());
				bigdataRepo.initialize();
			}
			if (repo == null) {
				sail = getIndexingSail();
				repo = new SailRepository(sail);
			}
		} catch (Exception e) {
			throw new ConnectionException(e.getMessage(), e);
		}
	}
	
	/**
	 * Get BigData journal file location from platform config and merge it with
	 * the repository connection properties.
	 *
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @throws ConfigurationException 
	 * @since Mar 8, 2012
	 */
	private Configuration loadProperties() throws ConfigurationException {
		PropertiesConfiguration config = PlatformConfigHelper.getConfig(); 
		properties = new PropertiesConfiguration("bigdata.properties");
		properties.setProperty(Options.FILE, config.getProperty("bigdata.journal"));
		return properties;
	}
	
	@Override
	public Sail getSail() throws ConnectionException {
		connect();
		return new BigdataSail(bigdataRepo);
	}
	
	@Override
	public Sail getIndexingSail() throws ConnectionException {
		Sail sail = new BigdataSail(bigdataRepo);		
		
		try {
			PostgisIndexerSettings settings = PostgisIndexerSettingsGenerator.generateSettings();
			IndexingSail idxSail = new IndexingSail(sail, settings);
			
//			IndexingSailConnection idxConn = idxSail.getConnection();
//			idxConn.reindex();
//			idxConn.commit();
//			idxConn.close();
			
			return idxSail;
		} catch (IndexException e) {
			logger.error(e.getMessage(), e);
//		} catch (SailException e) {
//			throw new ConnectionException(e.getMessage(), e);
		} catch (ConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Return read/write connection to the BigData repository.
	 *
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @throws ConfigurationException 
	 * @since Mar 8, 2012
	 */
	public SailRepositoryConnection getConnection() throws ConnectionException { 
//		BigdataSailRepositoryConnection conn = repo.getReadWriteConnection();
//		SailRepository repo = new SailRepository(sail);
//		SailRepositoryConnection conn = repo.getConnection();
		connect();
		SailRepositoryConnection conn;
		try {
			conn = repo.getConnection();
			conn.setAutoCommit(false);
			return conn;			
		} catch (RepositoryException e) {
			throw new ConnectionException(e.getMessage(), e);
		}
    }
	
	/**
	 * Return read-only connection to the BigData repository.
	 *
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @throws ConfigurationException 
	 * @since Mar 8, 2012
	 */
//	public RepositoryConnection getReadOnlyConnection() throws RepositoryException { 
//		BigdataSailRepositoryConnection conn = repo.getReadOnlyConnection();
//		return conn;
//	}	
	
	/**
	 * Return BigData repository instance.
	 * 
	 * @param properties
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Mar 8, 2012
	 */
	protected BigdataSailRepository createRepository(Configuration properties) {
		com.bigdata.rdf.sail.BigdataSail sail = new com.bigdata.rdf.sail.BigdataSail(
				ConfigurationConverter.getProperties(properties));
		return new BigdataSailRepository(sail);		
	}	
}
