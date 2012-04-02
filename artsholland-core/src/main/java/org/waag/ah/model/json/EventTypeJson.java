package org.waag.ah.model.json;

import org.codehaus.jackson.annotate.JsonProperty;

public abstract class EventTypeJson extends AHRDFObjectJson {
	
	@JsonProperty
	public abstract String getLabel();
			
}
