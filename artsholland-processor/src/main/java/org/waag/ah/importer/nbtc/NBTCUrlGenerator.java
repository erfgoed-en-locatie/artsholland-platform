package org.waag.ah.importer.nbtc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.UrlGenerator;

public class NBTCUrlGenerator implements UrlGenerator {

	private final List<URL> urls;
	private static final String[] RESOURCES = { 
//		"nbtc/ArtsHollandPois-test.xml",
		"nbtc/ArtsHollandPois-da_DK.xml",
		"nbtc/ArtsHollandPois-de_DE.xml",
		"nbtc/ArtsHollandPois-en_GB.xml",
		"nbtc/ArtsHollandPois-es_ES.xml",
		"nbtc/ArtsHollandPois-fr_FR.xml",
		"nbtc/ArtsHollandPois-it_IT.xml",
		"nbtc/ArtsHollandPois-nl_NL.xml",
		"nbtc/ArtsHollandPois-ru_RU.xml",
		"nbtc/ArtsHollandPois-sv_SE.xml",
	};	
	
	public NBTCUrlGenerator() throws MalformedURLException, ConfigurationException {
		PlatformConfig config = PlatformConfigHelper.getConfig();
		this.urls = new ArrayList<URL>();
		for (String url : RESOURCES) {
			this.urls.add(new URL(config.getString("platform.fileUri")+url));
		}
	}
	
	@Override
	public final List<URL> getUrls(ImportConfig config) {
		return urls;
	}
}
