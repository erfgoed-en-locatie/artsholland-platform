package org.waag.ah.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;

import org.waag.ah.PasswordAuthenticator;

public class URLTools {
	
	public static URL getAuthenticatedUrl(String url) throws MalformedURLException {
		return getAuthenticatedUrl(new URL(url));
	}
	
	public static URL getAuthenticatedUrl(URL url) throws MalformedURLException {
		boolean authenticated = false;
		String username = "";
		String password = "";
		
		String userInfo = url.getUserInfo();
		if (userInfo != null) {
			String[] usernamePassword = userInfo.split(":");
			if (usernamePassword.length == 2) {
				username = usernamePassword[0];
				password = usernamePassword[1];
				authenticated = true;
			}
		}		
		
		if (authenticated) {
			Authenticator.setDefault(new PasswordAuthenticator(username, password));
		} else {
			Authenticator.setDefault(null);
		}	
		
		return url;
	}
	
	public static String getUrlContent(URL url) throws IOException {
		InputStream is = null;
		BufferedReader in = null;
//	    DataInputStream dis;
	    StringBuilder content = new StringBuilder();
	    String s;
	    try {
			is = url.openStream();
			in = new BufferedReader(new InputStreamReader(is));
//			dis = new DataInputStream(new BufferedInputStream(is));
			while ((s = in.readLine()) != null) {
				content.append(s);
			}
			return content.toString();
	    } finally {
//	    	dis.close();
	    	in.close();
	    	is.close();
	    }
//		StringBuilder content = new StringBuilder();
//		URLConnection conn = url.openConnection();	
//		InputStream is = conn.getInputStream();
//        BufferedReader in = new BufferedReader(new InputStreamReader(is));
//        try {
//			String inputLine;
//			while ((inputLine = in.readLine()) != null) {
//				content.append(inputLine);
//			}
//			return content.toString();
//        } finally {
//        	in.close();
//        	is.close();
//        }
	}
}
