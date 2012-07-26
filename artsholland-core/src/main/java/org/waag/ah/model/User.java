package org.waag.ah.model;

import java.util.Date;
import java.util.List;

public interface User {
	public String getEmail();
	public Date getCreated();
	public String getName();
	public List<Application> getApplications();
}
