package org.waag.ah.model.json;

import java.math.BigDecimal;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonProperty;
import org.waag.ah.model.rdf.VenueType;

public abstract class VenueJson extends AHRDFObjectJson {

//	ah:attachment [http]	ah:venues/0669e8c5-75b2-47ce-bef3-f1cfd2acc192/attachments/1 [http]	-
//	ah:attachment [http]	ah:venues/0669e8c5-75b2-47ce-bef3-f1cfd2acc192/attachments/2 [http]	-
//	ah:cidn [http]	"0669e8c5-75b2-47ce-bef3-f1cfd2acc192"^^<xsd:string>	-
//	ah:disabilityInformation [http]	"Bereikbaarheid: er is een trein/metro/tram/bus in de buurt van de locatie (binnen 500 m), voorrijden bij entree is toegestaan.<BR> Toegankelijkheid:naast hoofdingang zijn er ook alternatieven ingangen, deurbreedte is minimaal 80 cm, lift aanwezig, zaal/zalen bereikbaar via lift.<BR> Speciale voorzieningen: rolstoelplaatsen aanwezig in zaal/zalen, locatie is geheel rookvrij."@nl	-
//	ah:openingHours [http]	"Kassa open een uur voor aanvang"@nl	-
//	ah:publicTransportInformation [http]	"(tram) 3; (bus) 35; (parkeren) parkeerterrein op de pieren vlakbij theater"@nl	-
//	ah:room [http]	ah:venues/0669e8c5-75b2-47ce-bef3-f1cfd2acc192/rooms/veemcaf%C3%A9 [http]	-
//	ah:shortDescription [http]	"Een productiehuis voor mime, beeldend theater en dans en presenteert daarnaast gastvoorstellingen uit binnen- en buitenland. Het is gevestigd op de 3e verdieping van een gerenoveerd 19e eeuws pakhuis met een schitterend uitzicht op het IJ."@nl	-
//	ah:venueType [http]	ah:VenueTypeTheater [http]	-
//	dc:created [http]	"2012-03-03T19:11:10Z"^^<xsd:string>	-
//	dc:description [http]	"Een productiehuis voor mime, beeldend theater en dans en presenteert daarnaast gastvoorstellingen uit binnen- en buitenland. Het is gevestigd op de 3e verdieping van een gerenoveerd 19e eeuws pakhuis met een schitterend uitzicht op het IJ."@nl	-
//	dc:title [http]	"veem theater"@nl	-
//	rdf:type [http]	ah:Venue [http]	-
//	owl:sameAs [http]	nub:locations/0669e8c5-75b2-47ce-bef3-f1cfd2acc192 [http]	-
//	geo:lat [http]	"52.3902473"^^<xsd:decimal>	-
//	geo:long [http]	"4.8862872"^^<xsd:decimal>	-
//	vcard:locality [http]	"Amsterdam"	-
//	vcard:postal-code [http]	"1013 CR"	-
//	vcard:street-address [http]	"Van Diemenstraat 410"	-
//	foaf:homepage [http]
	
	@JsonProperty()
	public abstract String getURI();
	
	@JsonProperty
	public abstract String getCidn();
	
	@JsonProperty
	public abstract String getTitle();
	
	@JsonProperty
	public abstract String getDescription();
	
	@JsonProperty
	public abstract String getShortDescription();
	
	@JsonProperty
	public abstract String getLocality();
	
	@JsonProperty
	public abstract String getPostalCode();
	
	@JsonProperty
	public abstract String getStreetAddress();
	
	@JsonProperty
	public abstract String getHomepage();

	@JsonProperty
	public abstract Set<RoomJson> getRooms();
	
	@JsonProperty
	public abstract Set<AttachmentJson> getAttachments();	
	
	@JsonProperty
	public abstract BigDecimal getLatitude();
	
	@JsonProperty
	public abstract BigDecimal getLongitude();
	
	@JsonProperty
	public abstract Set<String> getTags();
	
	@JsonProperty
	public abstract VenueType getVenueType();	
	
}
