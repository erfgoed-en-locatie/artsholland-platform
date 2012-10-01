package org.waag.ah.repository;

import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.RepositoryFactory;

import com.bigdata.journal.Options;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.useekm.bigdata.BigdataSail;

public class Bigdata implements RepositoryFactory {
	final static Logger logger = LoggerFactory.getLogger(Bigdata.class);

	private PlatformConfig config; 
	private PropertiesConfiguration properties;
	private Sail repo;

	@Override
	public synchronized Sail getSail() throws SailException {
		if (repo == null) {
			try {
				config = PlatformConfigHelper.getConfig();
				Properties properties = ConfigurationConverter.getProperties(loadProperties());
				BigdataSailRepository repository = new BigdataSailRepository(new com.bigdata.rdf.sail.BigdataSail(properties));
				repo = new BigdataSail(repository);
			} catch (ConfigurationException e) {
				throw new SailException(e);
			} 
		}
		return repo;
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
