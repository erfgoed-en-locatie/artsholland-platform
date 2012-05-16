package org.waag.ah.bigdata;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.java.ah.useekm.PostgisIndexerSettingsGenerator;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.RepositoryConnectionFactory;
import com.bigdata.journal.Options;
import com.bigdata.rdf.sail.BigdataSailRepository;
//import com.bigdata.rdf.sail.BigdataSail;
import com.useekm.bigdata.BigdataSail;
import com.useekm.indexing.IndexingSail;
import com.useekm.indexing.exception.IndexException;
import com.useekm.indexing.postgis.PostgisIndexerSettings;

@Singleton
public class BigdataConnectionService implements RepositoryConnectionFactory {
	final static Logger logger = LoggerFactory.getLogger(BigdataConnectionService.class);

	private BigdataSailRepository bigdataRepo;

	private PropertiesConfiguration properties;

	private Sail sail;
	
	private SailRepository repo; 
	
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
			bigdataRepo = createRepository(loadProperties());
			bigdataRepo.initialize();
			
			sail = getIndexingSail();
			repo = new SailRepository(sail);
			
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
	
	@Override
	public Sail getSail() {
		return new BigdataSail(bigdataRepo);
	}
	
	public Sail getIndexingSail() {
		
		Sail sail = getSail();		
		
		PostgisIndexerSettings settings = PostgisIndexerSettingsGenerator.generateSettings();
		
		IndexingSail idxSail = new IndexingSail(sail, settings);

		try {
			idxSail.getConnection().reindex();
		} catch (SailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return idxSail;		
		
	}
	
	
//	public Journal getJournal() {
//		return new Journal(ConfigurationConverter.getProperties(properties));
//	}
	
	/**
	 * Return read/write connection to the BigData repository.
	 *
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @throws ConfigurationException 
	 * @since Mar 8, 2012
	 */
	public SailRepositoryConnection getConnection() throws RepositoryException { 
//		BigdataSailRepositoryConnection conn = repo.getReadWriteConnection();
//		SailRepository repo = new SailRepository(sail);
//		SailRepositoryConnection conn = repo.getConnection();
		
		SailRepositoryConnection conn = repo.getConnection();
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
