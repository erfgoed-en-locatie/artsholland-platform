package org.waag.ah.model.rdf;

import java.math.BigDecimal;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;

@Iri(AHRDFObject.ah + "Venue")
public abstract class Venue extends AHRDFObject {

	@Iri(ah + "cidn")
	public abstract String getCidn();
	
	@Iri(dc + "title")
	public abstract Set<LangString> getTitles();
	
	@Iri(dc + "description")
	public abstract Set<LangString> getDescriptions();
	
	@Iri(ah + "shortDescription")
	public abstract  Set<LangString> getShortDescriptions();
	
	@Iri(ah + "room")
	public abstract Set<Room> getRooms();
	
	@Iri(ah + "attachment")
	public abstract Set<Attachment> getAttachments();
	
	@Iri(geo + "lat")
	public abstract BigDecimal getLatitude();
	
	@Iri(geo + "long")
	public abstract BigDecimal getLongitude();
		
	@Iri(vcard + "locality")
	public abstract String getLocality();
	
	@Iri(vcard + "postcal-code")
	public abstract String getPostalCode();
	
	@Iri(vcard + "street-address")
	public abstract String getStreetAddress();
	
	@Iri(foaf + "homepage")
	public abstract String getHomepage();
	
	@Iri(ah + "tag")
	public abstract Set<String> getTags();
	
	@Iri(ah + "venueType")
	public abstract VenueType getVenueType();	
	
}
