
package org.waag.ah.model.rdf;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.util.GenericType;

@Iri(RDFObject.ah + "Venue")
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public abstract class VenueImpl extends RDFObject implements Venue {

	@Iri(ah + "cidn")
	private String cidn;
	
	@Iri(dc + "title")
	private Set<LangString> titles;
	
	@Iri(dc + "description")
	private Set<LangString> descriptions;
	
	@Iri(ah + "room")
	private Set<Room> rooms;
	
	@Iri(geo + "lat")
	private BigDecimal latitude;
	
	@Iri(geo + "long")
	private BigDecimal longitude;
	
	@Override
	public String getTitle() {
		for (LangString title: titles) {
			return title.toString();
		}
		return null;
	}

	@Override
	public String getDescription() {
		for (LangString description: descriptions) {
			return description.toString();
		}
		return "";
	}

	@Override
	public Set<Room> getRooms() {
		return rooms;
	}
	
	@Override
	public String getCidn() {
		return cidn;
	}

	@Override	
	public BigDecimal getLatitude() {
		return latitude;
	}
	
	@Override
	public BigDecimal getLongitude() {
		return longitude;
	}

	
}
