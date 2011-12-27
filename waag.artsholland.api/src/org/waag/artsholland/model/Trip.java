package org.waag.artsholland.model;

import java.util.List;

public interface Trip extends Resource {
	public Profile getProfile();
	public List<TripActivity> getSchedule();
}
