
package org.waag.ah.importer.rce;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.UrlGenerator;

public class RCEURLGenerator implements UrlGenerator {
	final static Logger logger = LoggerFactory.getLogger(RCEURLGenerator.class);
	
	private final int MONUMENT_COUNT = 5000;
	private final int ROWS = 250;
	
	private final String RCE_URL = "http://api.rijksmonumenten.info/select/?q=rce_categorie:%22Openbare%20gebouwen%22";

	@Override
	public List<URL> getUrls(ImportConfig config) {
		List<URL> urls = new ArrayList<URL>();		
		try {
			int i = 0;
			while (i < MONUMENT_COUNT) {
				String url = RCE_URL +						
						"&rows=" + ROWS + 
						"&start=" + i;						
				urls.add(new URL(url));
				i += ROWS;
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}		
		return urls;
	}
}