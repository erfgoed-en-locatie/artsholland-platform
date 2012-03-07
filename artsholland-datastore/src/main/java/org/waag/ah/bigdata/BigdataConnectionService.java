package org.waag.ah.bigdata;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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
public class BigdataConnectionService implements RepositoryConnectionFactory {
	final static Logger logger = LoggerFactory.getLogger(BigdataConnectionService.class);
	
	private BigdataSailRepository repo;
	
	@PostConstruct
	public void create() {
		try {
			PropertiesConfiguration config = PlatformConfig.getConfig(); 
			PropertiesConfiguration properties = 
					new PropertiesConfiguration("bigdata.properties");
			properties.setProperty(Options.FILE, config.getProperty("bigdata.journal"));
			repo = createRepository(properties);
			repo.initialize();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public RepositoryConnection getConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = repo.getReadWriteConnection();
		conn.setAutoCommit(false);
		return conn;
    }
	
	public RepositoryConnection getReadOnlyConnection() throws RepositoryException { 
		BigdataSailRepositoryConnection conn = repo.getReadOnlyConnection();
		return conn;
	}	
	
	protected BigdataSailRepository createRepository(Configuration properties) {
		BigdataSail sail = new BigdataSail(ConfigurationConverter.getProperties(properties));
		return new BigdataSailRepository(sail);		
	}	
}
