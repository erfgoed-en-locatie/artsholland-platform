package org.waag.ah.bigdata;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.RepositoryConnectionFactory;

import com.bigdata.journal.Options;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection;

@Singleton
public final class BigdataConnectionService implements RepositoryConnectionFactory {
	final static Logger logger = LoggerFactory.getLogger(BigdataConnectionService.class);
	private BigdataSailRepository repo;
	
	/**
	 * Initialize BigData repository.
	 * 
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Mar 8, 2012
	 */
	@PostConstruct
	public void create() {
		try {
			repo = createRepository(loadProperties());
			repo.initialize();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
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
		PropertiesConfiguration config = PlatformConfig.getConfig(); 
		PropertiesConfiguration properties = 
				new PropertiesConfiguration("bigdata.properties");
		properties.setProperty(Options.FILE, config.getProperty("bigdata.journal"));
		return properties;
	}

	@Override
	public Repository getRepository() {
		return repo;
	}
	
	/**
	 * Return read/write connection to the BigData repository.
	 *
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @throws ConfigurationException 
	 * @since Mar 8, 2012
	 */
	public RepositoryConnection getConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = repo.getReadWriteConnection();
		conn.setAutoCommit(false);
		return conn;
    }
	
	/**
	 * Return read-only connection to the BigData repository.
	 *
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @throws ConfigurationException 
	 * @since Mar 8, 2012
	 */
	public RepositoryConnection getReadOnlyConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = repo.getReadOnlyConnection();
		return conn;
	}	
	
	/**
	 * Return BigData repository instance.
	 * 
	 * @param properties
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Mar 8, 2012
	 */
	protected BigdataSailRepository createRepository(Configuration properties) {
		BigdataSail sail = new BigdataSail(ConfigurationConverter.getProperties(properties));
		return new BigdataSailRepository(sail);		
	}
}
