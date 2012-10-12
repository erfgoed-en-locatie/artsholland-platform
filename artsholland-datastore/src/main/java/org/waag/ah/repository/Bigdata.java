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
import org.waag.rdf.sesame.SailFactory;

import com.bigdata.journal.Options;
import com.useekm.bigdata.BigdataSail;
import com.useekm.inference.SimpleTypeInferencingSail;

public class Bigdata implements SailFactory {
	final static Logger logger = LoggerFactory.getLogger(Bigdata.class);

	private PlatformConfig config; 
	private PropertiesConfiguration properties;
	private Sail sail;

	@Override
	public synchronized Sail getSail() throws SailException {
		if (sail == null) {
			try {
				config = PlatformConfigHelper.getConfig();
				Properties properties = ConfigurationConverter.getProperties(loadProperties());
//				BigdataSailRepository repository = new BigdataSailRepository(new com.bigdata.rdf.sail.BigdataSail(properties));
//				repo = new BigdataSail(repository);
				sail = new SimpleTypeInferencingSail(
//					   new SmartSailWrapper(
					   new BigdataSail(properties));
//				sail.setPipelineTypes(Arrays.asList(
//						"getReadOnlyConnection", 
//						"getReadWriteConnection",
//						"getUnisolatedConnection"));
			} catch (Exception e) {
				throw new SailException(e);
			} 
		}
		return sail;
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
