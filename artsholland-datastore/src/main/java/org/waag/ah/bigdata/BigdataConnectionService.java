package org.waag.ah.bigdata;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.Sail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.RepositoryConnectionFactory;

import com.bigdata.journal.Journal;
import com.bigdata.journal.Options;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection;
import com.useekm.bigdata.BigdataSail;

@Singleton
public class BigdataConnectionService implements RepositoryConnectionFactory {
	final static Logger logger = LoggerFactory.getLogger(BigdataConnectionService.class);

	private BigdataSailRepository repo;

	private PropertiesConfiguration properties;
	
	/**
	 * Initialize BigData repository.
	 * 
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Mar 8, 2012
	 */
	@PostConstruct
	public void create() {
		try {
			logger.info("CREATE REPOSITORY");
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
		PropertiesConfiguration config = PlatformConfigHelper.getConfig(); 
		properties = new PropertiesConfiguration("bigdata.properties");
		properties.setProperty(Options.FILE, config.getProperty("bigdata.journal"));
		return properties;
	}
	
	public Sail getSail() {
//		BigdataSailRepository repo = (BigdataSailRepository) cf.getRepository();
		return new BigdataSail(repo);
	}	

//	@Override
//	public Repository getRepository() {
//		return repo;
//	}
	
	public Journal getJournal() {
		return new Journal(ConfigurationConverter.getProperties(properties));
	}
	
	/**
	 * Return read/write connection to the BigData repository.
	 *
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @throws ConfigurationException 
	 * @since Mar 8, 2012
	 */
	public RepositoryConnection getConnection() throws RepositoryException { 
		logger.info("GET READWRITE CONNECTION");
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
		logger.info("GET READONLY CONNECTION");
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
		com.bigdata.rdf.sail.BigdataSail sail = new com.bigdata.rdf.sail.BigdataSail(
				ConfigurationConverter.getProperties(properties));
		return new BigdataSailRepository(sail);
	}
}
