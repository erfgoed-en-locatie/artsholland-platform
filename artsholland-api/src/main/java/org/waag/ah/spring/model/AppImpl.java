package org.waag.ah.spring.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.waag.ah.model.App;

@Document(collection="app")
public class AppImpl implements App {

	@Id
	private String apiKey;
	private String secret;
	private String name;
	private String title;
	private String url;
	private String description;
	
	@Override
	public String getApiKey() {
		return apiKey;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getURL() {
		return url;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setURL(String url) {
		this.url = url;
	}

}
