package org.waag.ah.source.uitbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UitbaseURLGenerator {
	final static Logger logger = LoggerFactory.getLogger(UitbaseURLGenerator.class);
	private final String BASE_URL;
	private final String API_KEY;
	
	private static final int ROWS = 500;
	
	public static final String[] RESOURCES = { 
		"events", 
		"locations", 
		"productions",
		"groups" 
	};
	
	public UitbaseURLGenerator(String endpoint, String apiKey) {
		BASE_URL = endpoint + "/search";
		API_KEY = apiKey;
	}	
	
	public List<URL> getURLs() throws IOException {
		return getURLs(null, null);
	}

	public List<URL> getURLs(DateTime dtTo, DateTime dtFrom) throws IOException {
		List<URL> urls = new ArrayList<URL>();
		
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		
		String dtToParam = dtTo != null ? "&createdto="+fmt.withZone(DateTimeZone.UTC).print(dtTo) : "";
		String dtFromParam = dtFrom != null ? "&createdfrom="+fmt.withZone(DateTimeZone.UTC).print(dtFrom) : "";
		
		//dtFromParam = "&createdfrom=2012-06-19T14:41:59.998Z";
		
		for (String resource : RESOURCES) {
			String resourceFilterParam = "&filter=resource:" + resource;
			
			int count = 0;
			try {
				String content = readURL(getCountURL(dtFromParam + dtToParam + resourceFilterParam));
				count = getCount(content);	
			} catch (IOException e) {			
				e.printStackTrace();
			}
	
			int i = 0;
			while (i < count) {
				// TODO: use something like URLBuilder 
				String url = addAPIKey(BASE_URL) + 
						"&resolve=true" +
						"&rows=" + ROWS + 
						"&start=" + i + 
						dtFromParam + dtToParam +
						resourceFilterParam;
				urls.add(new URL(url));
				i += ROWS;
			}
		}
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
		return addAPIKey(BASE_URL) + "&rows=0" + filter;
	}

	private String addAPIKey(String url) {
		return url + "?key=" + API_KEY;
	}

	private static String readURL(String url) throws IOException {
		/*
		 * HttpClient client = new HttpClient(); method = new
		 * GetMethod(baseUrl); method.getResponseBodyAsString()
		 */

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new URL(url).openStream()));

		StringBuilder sb = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();

		return sb.toString();
	}
}
