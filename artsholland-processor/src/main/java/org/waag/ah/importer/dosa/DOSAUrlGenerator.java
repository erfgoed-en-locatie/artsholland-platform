package org.waag.ah.importer.dosa;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.UrlGenerator;

public class DOSAUrlGenerator implements UrlGenerator {
	
	private final List<URL> urls;
	private static final String[] RESOURCES = { 
		"http://127.0.0.1/ah/dosa/2010_oov.xls",
		"http://127.0.0.1/ah/dosa/2011_cm.xls",
		"http://127.0.0.1/ah/dosa/2011_wie.xls"
	};	
	
	public DOSAUrlGenerator() throws MalformedURLException {
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
