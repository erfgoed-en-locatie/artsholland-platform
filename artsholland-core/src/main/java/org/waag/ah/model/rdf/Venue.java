package org.waag.ah.model.rdf;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.openrdf.repository.object.LangString;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public interface Venue extends org.openrdf.repository.object.RDFObject {

//	@JsonProperty
	public String getCidn();
	
	@JsonProperty
	public String getTitle();
	
	@JsonProperty
	public String getDescription();

	@JsonProperty
	@JsonSerialize(include=Inclusion.NON_EMPTY)
	public Set<Room> getRooms();
	
	@JsonProperty
	public double getLatitude();
	
	@JsonProperty
	public double getLongitude();
	
}
