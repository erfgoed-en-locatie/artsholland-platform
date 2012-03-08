package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonSerialize(include=Inclusion.NON_EMPTY)
public interface Production extends org.openrdf.repository.object.RDFObject {
	
	@JsonProperty
	public String getTitle();
	
	@JsonProperty
	public String getCidn();
		
}
