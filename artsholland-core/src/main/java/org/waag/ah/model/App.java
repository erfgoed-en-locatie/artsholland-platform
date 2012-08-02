package org.waag.ah.model;

public interface App {
	public String getApiKey();
	public void setApiKey(String apiKey);
	
	public User getUser();
	public void setUser(User user);
	
	public String getSecret();
	public void setSecret(String secret);
	
	public String getName();
	public void setName(String name);
	
	public String getTitle();
	public void setTitle(String title);
	
	public String getURL();
	public void setURL(String url);
	
	public String getDescription();
	public void setDescription(String description);
}
