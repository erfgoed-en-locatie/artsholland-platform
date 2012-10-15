
package org.waag.ah.importer.tam;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.ImportStrategy;
import org.waag.ah.importer.UrlGenerator;

public class TAMURLGenerator implements UrlGenerator {
	final static Logger logger = LoggerFactory.getLogger(TAMURLGenerator.class);
	
	//private final PlatformConfig config;
	private final String baseUrl;
		
	public TAMURLGenerator() throws ConfigurationException {
		//this.config = PlatformConfigHelper.getConfig();

		// http://acc.artsholland.com/api?timestamp=1349605041
		// user: artsholland
		// password: choose only one master nature
			
		this.baseUrl = "http://artsholland:choose only one master nature@acc.artsholland.com/api";
	}	

	@Override
	public List<URL> getUrls(ImportConfig config) {
		List<URL> urls = new ArrayList<URL>();
									
		if (config.getStrategy().equals(ImportStrategy.INCREMENTAL) && config.getFromDateTime() != null) {			
			long timestamp = config.getFromDateTime().getMillis();
			
			String url = baseUrl
					+ "?timestamp="
					+ timestamp;
			
			//url = "http://localhost/ah/tam/export.xml";
			
			try {
				urls.add(new URL(url));
			} catch (MalformedURLException e) {
				throw new RuntimeException(e.getMessage(), e);
			}			
		}		
		return urls;
	}
}