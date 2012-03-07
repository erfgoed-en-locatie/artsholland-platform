package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public interface Production extends org.openrdf.repository.object.RDFObject {
	
	@JsonProperty
	public String getTitle();
		
}
