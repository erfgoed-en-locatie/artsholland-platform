package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonProperty;

public interface Genre extends AHRDFObject {
	
	@JsonProperty
	public String getLabel();
			
}
