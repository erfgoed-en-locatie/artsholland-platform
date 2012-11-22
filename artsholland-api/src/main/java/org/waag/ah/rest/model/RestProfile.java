package org.waag.ah.rest.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.waag.ah.model.Profile;

public class RestProfile implements Profile {
	private String uri;

	public RestProfile(String uri) {
		this.uri = uri;
	}

	@Override
	public URL getUrl() {
		try {
			return new URL(uri);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return null;
	}

	public void setFacebookCredentials(String fbId, String fbAuthKey) {
	}
}
