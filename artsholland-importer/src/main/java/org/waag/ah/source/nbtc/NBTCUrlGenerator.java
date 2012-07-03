package org.waag.ah.source.nbtc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.UrlGenerator;

public class NBTCUrlGenerator implements UrlGenerator {

	private final List<URL> urls;
	private static final String[] RESOURCES = { 
//		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-test.xml",
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-da_DK.xml",
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-de_DE.xml",
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-en_GB.xml",
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-es_ES.xml",
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-fr_FR.xml",
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-it_IT.xml",
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-nl_NL.xml",
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-ru_RU.xml",
		"http://127.0.0.1/ah/nbtc/ArtsHollandPois-sv_SE.xml",
	};	
	
	public NBTCUrlGenerator() throws MalformedURLException {
		this.urls = new ArrayList<URL>();
		for (String url : RESOURCES) {
			this.urls.add(new URL(url));
		}
	}
	
	@Override
	public final List<URL> getUrls(ImportConfig config) {
		return urls;
	}
}
