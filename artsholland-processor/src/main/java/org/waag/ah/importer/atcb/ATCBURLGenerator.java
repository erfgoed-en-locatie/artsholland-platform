package org.waag.ah.importer.atcb;

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
import org.waag.ah.importer.UrlGenerator;

public class ATCBURLGenerator implements UrlGenerator {
	final static Logger logger = LoggerFactory.getLogger(ATCBURLGenerator.class);

	private final PlatformConfig config;
	private final String endpoint;
	private final String username;
	private final String password;
	
	public ATCBURLGenerator() throws ConfigurationException {
		this.config = PlatformConfigHelper.getConfig();
		this.endpoint = config.getString("importer.source.atcb.endpoint");
		this.username = config.getString("importer.source.atcb.username");
		this.password = config.getString("importer.source.atcb.password");
	}

	@Override
	public List<URL> getUrls(ImportConfig config) {
		List<URL> urls = new ArrayList<URL>();			
		
		try {
			urls.add(new URL("https://dl.dropboxusercontent.com/u/12905316/atcb/Attracties.xml"));
			urls.add(new URL("https://dl.dropboxusercontent.com/u/12905316/atcb/EtenDrinken.xml"));
			urls.add(new URL("https://dl.dropboxusercontent.com/u/12905316/atcb/Evenementen.xml"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return urls;
	}
	
}