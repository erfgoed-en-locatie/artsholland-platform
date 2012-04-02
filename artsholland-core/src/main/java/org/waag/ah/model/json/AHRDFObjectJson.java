package org.waag.ah.model.json;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonSerialize(include=Inclusion.NON_EMPTY)
public abstract class AHRDFObjectJson { 	
	
	@JsonProperty
	public abstract String getLabel();
	
	public abstract ObjectConnection getObjectConnection();

	public abstract Resource getResource();
	
	public abstract String getURI();
	
}
