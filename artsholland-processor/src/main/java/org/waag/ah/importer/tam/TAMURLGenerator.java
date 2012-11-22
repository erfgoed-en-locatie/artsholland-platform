
package org.waag.ah.importer.tam;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.ImportStrategy;
import org.waag.ah.importer.UrlGenerator;

public class TAMURLGenerator implements UrlGenerator {
	final static Logger logger = LoggerFactory.getLogger(TAMURLGenerator.class);
	
	private final PlatformConfig config;
	private final String endpoint;
		
	public TAMURLGenerator() throws ConfigurationException {
		this.config = PlatformConfigHelper.getConfig();
		this.endpoint = config.getString("importer.source.tam.endpoint");
		
		// http://username:password@acc.artsholland.com/api?timestamp=1349605041			
	}	

	@Override
	public List<URL> getUrls(ImportConfig config) {
		List<URL> urls = new ArrayList<URL>();
									
		long timestamp = 0;
		if (config.getStrategy().equals(ImportStrategy.INCREMENTAL) && config.getFromDateTime() != null) {
			timestamp = config.getFromDateTime().getMillis() / 1000;
		} 
		
		// TODO: Niet zo mooi, maar soit.
		String url = endpoint + "?timestamp=" + timestamp;
				
		try {
			urls.add(new URL(url));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}			
			
		return urls;
	}
}