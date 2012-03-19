package org.waag.ah.model.json;

import org.codehaus.jackson.annotate.JsonProperty;

public abstract class EventStatusJson extends AHRDFObjectJson {
	
	@JsonProperty
	public abstract String getLabel();
			
}
