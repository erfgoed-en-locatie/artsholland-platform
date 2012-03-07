package org.waag.ah;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformConfig extends PropertiesConfiguration {
	final static Logger logger = LoggerFactory.getLogger(PlatformConfig.class);
	private static PropertiesConfiguration platformConfig;
	
	private PlatformConfig() {}
	
	public synchronized static PropertiesConfiguration getConfig() 
			throws ConfigurationException {
		if (platformConfig == null) {
			String file = "file://"+System.getProperty("user.home")+"/.artsholland/artsholland.properties";
			platformConfig = new PropertiesConfiguration(file);
		}
		return platformConfig;
	}
}
