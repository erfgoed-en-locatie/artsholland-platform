package org.waag.ah.model.rdf;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonProperty;

public interface Production extends AHRDFObject {
	
//	ah:cidn [http]	"2011-P-001-0078420"^^<xsd:string>	-
//	ah:genre [http]	ah:GenrePopmusic [http]	-
//	ah:party [http]	"Matangi Quartet"@nl	-
//	ah:people [http]	"Matangi Quartet"@nl	-
//	ah:productionType [http]	ah:ProductionTypePerformance [http]	-
//	dc:description [http]	"'Testimoni Interiori' was Martin Fondse's eerste compositie voor Eric Vloeimans en Matangi Quartet. Nu vult hij dat werk aan met 'Testimoni Exteriori', een nieuwe reeks meer extraverte composities waarin de nieuwe liefde van Fondse centraal staat: de vibrandoneon. Dit instrument ziet eruit als de barokversie van de melodica en is qua dynamische en emotionele eigenschappen te vergelijken met de bandoneon."@nl	-
//  ah:shortDescription	
//	rdf:type [http]	ah:Production [http]	-
//	owl:sameAs [http]	nub:productions/2011-P-001-0078420 [http]	-
//	foaf:homepage [http]	"www.dekleinekomedie.nl"^^<xsd:string>
	
	@JsonProperty
	public String getTitle();
	
	@JsonProperty
	public String getDescription();
	
	@JsonProperty
	public String getCidn();
	
	@JsonProperty
	public String getHomepage();

	public Set<Event> getEvents();
		
}
