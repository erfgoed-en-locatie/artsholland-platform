package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonProperty;

public interface Attachment extends AHRDFObject {
	
	@JsonProperty
	public String getTitle();
	
	@JsonProperty
	public String getDescription();
	
	//@JsonProperty
	public String getDepiction();
			
}
