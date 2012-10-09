
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

		this.baseUrl = "http://acc.artsholland.com/api";
		// http://acc.artsholland.com/api?timestamp=1349605041\
		// user: artsholland
		// password: choose only one master nature
		
		//this.baseUrl = config.getString("importer.source.uitbase.v4.endpoint")+"/search";
		//this.apiKey = config.getString("importer.source.uitbase.v4.apiKey");		
	}	

	@Override
	public List<URL> getUrls(ImportConfig config) {
		List<URL> urls = new ArrayList<URL>();
									
		if (config.getStrategy().equals(ImportStrategy.INCREMENTAL) && config.getFromDateTime() != null) {			
			long timestamp = config.getFromDateTime().getMillis();
			
			String url = baseUrl
					+ "?timestamp="
					+ timestamp;
			
			url = "http://localhost/ah/tam/export.xml";
			//url = "http://127.0.0.1/ah/tam/export_2.xml";
			url = "https://dl.dropbox.com/u/12905316/export.xml";
			//url = "https://dl.dropbox.com/u/12905316/export_2.xml";
			//url = "https://dl.dropbox.com/u/12905316/exp_nub.xml";
			//url = "http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&resource=events&rows=50&start=14500";
			//url = "http://ps4.uitburo.nl/api/locations/25f69cc1-428c-4b49-aff3-b493f5a17a00?key=505642b12881b9a60688411a333bc78b&resolve";
			
			try {
				urls.add(new URL(url));
			} catch (MalformedURLException e) {
				throw new RuntimeException(e.getMessage(), e);
			}			
		}		
		return urls;
	}
}