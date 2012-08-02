package org.waag.ah.bigdata;

import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.RepositoryConnectionFactory;
import org.waag.ah.useekm.PostgisIndexerSettingsGenerator;

import com.bigdata.journal.Options;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.useekm.bigdata.BigdataSail;
import com.useekm.indexing.IndexingSail;
import com.useekm.indexing.postgis.PostgisIndexerSettings;

@Singleton
public class BigdataConnectionService implements RepositoryConnectionFactory {
	final static Logger logger = LoggerFactory.getLogger(BigdataConnectionService.class);

	private PlatformConfig config; 
	private PropertiesConfiguration properties;
	private SailRepository repo;
	private SailRepositoryConnection conn;

	@PostConstruct
	public synchronized void connect() {
		
		try {
			config = PlatformConfigHelper.getConfig(); 
			
			Properties properties = ConfigurationConverter.getProperties(loadProperties());
			PostgisIndexerSettings settings = PostgisIndexerSettingsGenerator.generateSettings();
			
			BigdataSailRepository repository = new BigdataSailRepository(new com.bigdata.rdf.sail.BigdataSail(properties));
			BigdataSail sail = new BigdataSail(repository);
			IndexingSail idxSail = new IndexingSail(sail, settings);
			
			repo = new SailRepository(idxSail);
			repo.initialize();
			
			conn = repo.getConnection();
			conn.setAutoCommit(false);
			
			String uri = config.getString("platform.classUri").replaceAll("\\/$", "");
			URLConnection con = new URL(uri).openConnection();
			con.connect();
//			System.out.println( "redirected url: " + con.getURL() );
			
			conn.add(con.getInputStream(), uri, RDFFormat.RDFXML);
			conn.commit();
			
//			IndexingSailConnection idxConn = idxSail.getConnection();
//			idxConn.reindex();
//			idxConn.commit();
//			idxConn.close();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * Return read/write connection to the BigData repository.
	 *
	 * @author Raoul Wissink <raoul@raoul.net>
	 * @throws ConfigurationException 
	 * @since Mar 8, 2012
	 */
	@Override
	public SailRepositoryConnection getConnection() { 
		return conn;			
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
		properties = new PropertiesConfiguration("bigdata.properties");
		properties.setProperty(Options.FILE, config.getProperty("bigdata.journal"));
		return properties;
	}
}
