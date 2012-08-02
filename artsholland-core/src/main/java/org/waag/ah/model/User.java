package org.waag.ah.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface User {
	public String getEmail();
	public void setEmail(String email);
	
	public Date getCreated();
	public void setCreated(Date created);
	
	public String getName();
	public void setName(String name);
	
	public Collection<App> getApps();
	
}
