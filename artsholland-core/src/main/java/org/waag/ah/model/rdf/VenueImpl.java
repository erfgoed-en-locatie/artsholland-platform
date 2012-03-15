package org.waag.ah.model.rdf;

import java.math.BigDecimal;
import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;

@Iri(AHRDFObjectImpl.ah + "Venue")
public class VenueImpl extends AHRDFObjectImpl implements Venue {

	@Iri(ah + "cidn")
	private String cidn;
	
	@Iri(dc + "title")
	private Set<LangString> titles;
	
	@Iri(dc + "description")
	private Set<LangString> descriptions;
	
	@Iri(ah + "shortDescription")
	private Set<LangString> shortDescriptions;
	
	@Iri(ah + "room")
	private Set<Room> rooms;
	
	@Iri(ah + "attachment")
	private Set<Attachment> attachments;
	
	@Iri(geo + "lat")
	private BigDecimal latitude;
	
	@Iri(geo + "long")
	private BigDecimal longitude;
		
	@Iri(vcard + "locality")
	private String locality;
	
	@Iri(vcard + "postcal-code")
	private String postalCode;
	
	@Iri(vcard + "street-address")
	private String streetAddress;
	
	@Iri(foaf + "homepage")
	private String homepage;
	
	@Override
	public String getTitle() {
		return getLangString(titles);
	}

	@Override
	public String getDescription() {
		return getLangString(descriptions);
	}

	@Override
	public Set<Room> getRooms() {
		return rooms;
	}
	
	@Override
	public Set<Attachment> getAttachments() {
		return attachments;
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
	
	@Override
	public String getShortDescription() {
		return getLangString(shortDescriptions);
	}
	
	@Override
	public String getLocality() {
		return locality;
	}
	
	@Override
	public String getPostalCode() {
		return postalCode;
	}
	
	@Override
	public String getStreetAddress() {
		return streetAddress;
	}
	
	@Override
	public String getHomepage() {
		return homepage;
	}

	
}
