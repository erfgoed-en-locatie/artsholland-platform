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
	
	@Override
	public String getShortDescription() {
		for (LangString shortDescription: shortDescriptions) {
			return shortDescription.toString();
		}
		return "";
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
