package org.waag.ah.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
		StringBuilder content = new StringBuilder();
		URLConnection conn = url.openConnection();	
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		return content.toString();
	}
}
