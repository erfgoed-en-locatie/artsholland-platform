package org.waag.ah.rest.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.waag.ah.model.Profile;

public class RestProfile implements Profile {
	private String uri;
	private Long fbId;
//	private String fbAuthKey;

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

	public void setFacebookCredentials(long fbId, String fbAuthKey) {
		this.fbId = fbId;
//		this.fbAuthKey = fbAuthKey;
	}
	
	public long getFbId() {
		return this.fbId;
	}
}
