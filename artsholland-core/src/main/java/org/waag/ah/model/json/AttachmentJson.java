package org.waag.ah.model.json;

import org.codehaus.jackson.annotate.JsonProperty;
import org.openrdf.model.URI;
import org.waag.ah.model.rdf.AttachmentType;

public abstract class AttachmentJson extends AHRDFObjectJson {
	
	@JsonProperty
	public abstract String getTitle();
	
	@JsonProperty
	public abstract String getDescription();
	
	//@JsonProperty
	public abstract URI getUrl();
	
	@JsonProperty
	public abstract AttachmentType getAttachmentType();
			
}
