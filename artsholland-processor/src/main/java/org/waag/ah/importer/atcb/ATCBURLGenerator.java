package org.waag.ah.importer.atcb;

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
	
	public static final String[] RESOURCES = { 
		"Activiteiten", 
		"EtenDrinken",     
		"Festivals",        
		"Shoppen",         
		"Theater",
		"Attracties",     
		"Evenementen",    
		"MuseaGalleries",   
		"Tentoonstellingen", 
		"UitInAmsterdam",
	};
	
	public ATCBURLGenerator() throws ConfigurationException {
		this.config = PlatformConfigHelper.getConfig();
		this.endpoint = config.getString("importer.source.atcb.endpoint");
	}

	@Override
	public List<URL> getUrls(ImportConfig config) {
		List<URL> urls = new ArrayList<URL>();		
		try {
			for (String resource : RESOURCES) {				
				urls.add(new URL(this.endpoint + resource + ".xml"));				
			}
		} catch (Exception unimportant) {}		
		return urls;
	}
	
}