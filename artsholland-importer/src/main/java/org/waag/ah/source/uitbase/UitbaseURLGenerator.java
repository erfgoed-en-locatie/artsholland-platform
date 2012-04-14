package org.waag.ah.source.uitbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
	
//	public static final String[] RESOURCES = { 
//		"events", 
//		"locations", 
//		"productions",
//		"groups" 
//	};
	
	public UitbaseURLGenerator(String endpoint, String apiKey) {
		BASE_URL = endpoint + "/search";
		API_KEY = apiKey;
	}	

	public List<URL> getURLs(DateTime dt) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		String dtParam = dt != null ? "&createdfrom="+fmt.withZone(DateTimeZone.UTC).print(dt) : "";
		
//		for (String resource : RESOURCES) {
		int count = 0;
		try {
			String content = readURL(getCountURL(dtParam));
			count = getCount(content);	
		} catch (IOException e) {			
			e.printStackTrace();
		}
		int i = 0;
		while (i < count) {
			// TODO: use something like URLBuilder 
//			String url = addAPIKey(BASE_URL + resource) + "&rows=" + ROWS + "&start=" + i + dtParam;
			String url = addAPIKey(BASE_URL) + 
					"&resolve=true" +
//					"&resource=" + resource +
					"&rows=" + ROWS + 
					"&start=" + i + 
					dtParam;
			urls.add(new URL(url));
			i += ROWS;
		}
//		}		
		return urls;
	}
		
	public List<URL> getURLs() throws MalformedURLException {
		return getURLs(null);
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
		return addAPIKey(BASE_URL) + "&rows=0"+filter;
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
