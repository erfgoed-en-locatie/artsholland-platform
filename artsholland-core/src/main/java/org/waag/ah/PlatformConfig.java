package org.waag.ah;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Platform configuration helper class.
 *  
 * @author Raoul Wissink <raoul@raoul.net>
 */
public class PlatformConfig extends PropertiesConfiguration {
	final static Logger logger = LoggerFactory.getLogger(PlatformConfig.class);
	private static PropertiesConfiguration platformConfig;
	
	private PlatformConfig() {}
	
	/**
	 * Return platform configuration instance.
	 * 
	 * @throws ConfigurationException
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Mar 8, 2012
	 */
	public synchronized static PropertiesConfiguration getConfig() 
			throws ConfigurationException {
		if (platformConfig == null) {
			String file = "file://"+System.getProperty("user.home")+"/.artsholland/artsholland.properties";
			platformConfig = new PropertiesConfiguration(file);
		}
		return platformConfig;
	}
}
