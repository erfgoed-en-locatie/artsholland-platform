package org.waag.ah.source.uitbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UitbaseURLGenerator {

	//	http://accept.ps4.uitburo.nl/api/productions?key=505642b12881b9a60688411a333bc78b&rows=1&start=10
	//
	//	<productions>
	//		<hits>2896</hits>
	//		<rows>0</rows>
	//		<start>0</start>
	//	</productions>
	
	private static final String BASE_URL = "http://accept.ps4.uitburo.nl/api/";
	private static final String API_KEY =	"505642b12881b9a60688411a333bc78b";
	
	private static final int ROWS = 500;
	
	private static final String[] RESOURCES = { 
		"events", 
		"locations", 
		"productions",
		"groups" 
	};
		
	public static List<URL> getURLs() throws MalformedURLException {
		
		List<URL> urls = new ArrayList<URL>();
		
		for (String resource : RESOURCES) {
			int count = 0;
			try {
				
				String content = readURL(getCountURL(resource));
				count = getCount(content);	
				
			} catch (IOException e) {			
				e.printStackTrace();
			}
			int i = 0;
			while (i < count) {
				
				// TODO: use something like URLBuilder 
				String url = addAPIKey(BASE_URL + resource) + "&rows=" + ROWS + "&start=" + i;
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

	private static String getCountURL(String resource) {
		return addAPIKey(BASE_URL + resource) + "&rows=0";
	}

	private static String addAPIKey(String url) {
		return url + "?key=" + API_KEY;
	}

	private static String readURL(String url) throws IOException {		
    /*
		HttpClient client = new HttpClient();
		method = new GetMethod(baseUrl);
		method.getResponseBodyAsString()
		*/
		
		BufferedReader reader = new BufferedReader(
    		new InputStreamReader(new URL(url).openStream()));  
    
    StringBuilder sb = new StringBuilder();
    
  	String line;
  	while ((line = reader.readLine()) != null) {
  		sb.append(line);
  	} 
  	reader.close();
  	
  	return sb.toString();  	
	}
	
}
