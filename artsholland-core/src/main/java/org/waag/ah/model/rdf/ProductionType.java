package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonProperty;

public interface ProductionType extends AHRDFObject {
	
	@JsonProperty
	public String getLabel();
			
}
