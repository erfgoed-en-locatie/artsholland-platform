package org.waag.ah.model.rdf;

import java.math.BigDecimal;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonSerialize(include=Inclusion.NON_EMPTY)
public interface Venue extends org.openrdf.repository.object.RDFObject {

//	ah:cidn [http]	"0100d220-0077-400a-9bc4-6d222b97deba"^^<xsd:string>	-
//	ah:shortDescription [http]	"De Wijn & Cultuurhoeve Thabor is een initiatief van Auke Age de Jong en Karin Idzenga."@nl	-
//	ah:venueType [http]	ah:VenueTypeMuseum [http]	-
//	dc:created [http]	"2012-03-03T17:28:32Z"^^<xsd:string>	-
//	dc:description [http]	"De Wijn & Cultuurhoeve Thabor is een initiatief van Auke Age de Jong en Karin Idzenga. Auke is opgegroeid op boerderij Thabor en heeft van het agrarische bedrijf een wijnhoeve gemaakt. De passie voor goede wijnen heeft hij onder andere opgedaan in wijnland Argentini� waar hij gewoond en gestudeerd heeft. <BR><BR> Karin Idzenga heeft de theateropleiding in Arnhem gevolgd en schreef en regisseerde o.a. het openluchtspel in Dronrijp en andere voorstellingen. Daarnaast is zij docent in theaterlessen en haar andere grote liefde: het schilderen."@nl	-
//	dc:title [http]	"Wijn en Cultuurhoeve Thabor"@nl	-
//	rdf:type [http]	ah:Venue [http]	-
//	owl:sameAs [http]	nub:locations/0100d220-0077-400a-9bc4-6d222b97deba [http]	-
//	geo:lat [http]	"0.0"^^<xsd:decimal>	-
//	geo:long [http]	"0.0"^^<xsd:decimal>	-
//	vcard:locality [http]	"Ysbrechtum"	-
//	vcard:postal-code [http]	"8633 WS"
	
	@JsonProperty
	public String getCidn();
	
	@JsonProperty
	public String getTitle();
	
	@JsonProperty
	public String getDescription();

	@JsonProperty
	public Set<Room> getRooms();
	
	@JsonProperty
	public BigDecimal getLatitude();
	
	@JsonProperty
	public BigDecimal getLongitude();
	
}
