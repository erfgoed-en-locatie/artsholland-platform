package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonProperty;

public interface EventStatus extends AHRDFObject {
	
	@JsonProperty
	public String getLabel();
			
}
