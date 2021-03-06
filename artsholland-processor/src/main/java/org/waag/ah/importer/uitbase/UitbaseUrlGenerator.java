package org.waag.ah.importer.uitbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.importer.ImportConfig;
import org.waag.ah.importer.ImportStrategy;
import org.waag.ah.importer.UrlGenerator;

public class UitbaseUrlGenerator implements UrlGenerator {
	final static Logger logger = LoggerFactory.getLogger(UitbaseUrlGenerator.class);
	
	private final PlatformConfig config;
	private final String baseUrl;
	private final String apiKey;
	private final int ROWS = 500;
	
	public static final String[] RESOURCES = { 
		"events", 
		"locations", 
		"productions",
		"groups"
	};
	
	public UitbaseUrlGenerator() throws ConfigurationException {
		this.config = PlatformConfigHelper.getConfig();
		this.baseUrl = config.getString("importer.source.uitbase.v4.endpoint")+"/search";
		this.apiKey = config.getString("importer.source.uitbase.v4.apiKey");		
	}	

	@Override
	public List<URL> getUrls(ImportConfig config) {
		List<URL> urls = new ArrayList<URL>();
		
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
				
//		String dtToParam = dtTo != null ? String.format("&%sto=%s", UITBASE_OBJECT_PROPERTY_MODIFIED, fmt.withZone(DateTimeZone.UTC).print(dtTo)) : "";
//		String dtFromParam = dtFrom != null ? String.format("&%sfrom=%s", UITBASE_OBJECT_PROPERTY_MODIFIED, fmt.withZone(DateTimeZone.UTC).print(dtFrom)) : "";
		
		String dtFromParam = "";
		String dtToParam = "";
		
		if (config.getStrategy().equals(ImportStrategy.INCREMENTAL) && config.getFromDateTime() != null) {
			dtFromParam = "&modifiedfrom="+fmt.withZone(DateTimeZone.UTC).print(config.getFromDateTime());
		}
		if (config.getToDateTime() != null) {
			dtToParam = "&modifiedto="+fmt.withZone(DateTimeZone.UTC).print(config.getToDateTime());
		}
		
		for (String resource : RESOURCES) {
			String resourceFilterParam = "&filter=resource:" + resource;
			
			int count = 0;
			try {
				String content = readURL(getCountURL(dtFromParam + dtToParam + resourceFilterParam));
				count = getCount(content);	
			} catch (IOException e) {			
				e.printStackTrace();
			}
	
			try {
				int i = 0;
				while (i < count) {
					// TODO: use something like URLBuilder 
					String url = addAPIKey(baseUrl) + 
							"&resolve=true" +
							"&rows=" + ROWS + 
							"&start=" + i + 
							dtFromParam + dtToParam +
							resourceFilterParam;
					urls.add(new URL(url));
					i += ROWS;
				}
			} catch (MalformedURLException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
//		ArrayList<URL> test = new ArrayList<URL>();
//		try {
//			test.add(new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=1&start=0&modifiedto=2013-06-01T21:57:00.002Z&filter=resource:events"));
//			test.add(new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=1&start=1&modifiedto=2013-06-01T21:57:00.002Z&filter=resource:events"));
//			test.add(new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=1&start=2&modifiedto=2013-06-01T21:57:00.002Z&filter=resource:events"));
////			test.add(new URL("http://ps4.uitburo.nl/api/search?key=505642b12881b9a60688411a333bc78b&resolve=true&rows=500&start=0&modifiedfrom=2013-03-22T15:32:00.001Z&modifiedto=2013-06-22T15:33:00.000Z&filter=resource:productions"));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		return test;
//		try {
//			return urls.subList(0, 2);
//		} catch(IndexOutOfBoundsException e) {}
		return urls;
	}
	
	private static int getCount(String content) {
		final String start = "<hits>";
		final String end = "</hits>";

		int startPos = content.indexOf(start);
		int endPos = content.indexOf(end);
		
		if (startPos > 0 && endPos > 0) {
			String count = content.substring(startPos + start.length(), endPos);
			return Integer.parseInt(count);
		}
		
		return 0;
	}

	private String getCountURL(String filter) {
		return addAPIKey(baseUrl) + "&rows=0" + filter;
	}

	private String addAPIKey(String url) {
		return url + "?key=" + apiKey;
	}

	private static String readURL(String url) throws IOException {
		/*
		 * HttpClient client = new HttpClient(); method = new
		 * GetMethod(baseUrl); method.getResponseBodyAsString()
		 */

		InputStream is = new URL(url).openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		try {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} finally {
			reader.close();
			is.close();
		}
	}
}
