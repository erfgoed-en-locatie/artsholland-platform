package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(AHRDFObjectImpl.ah + "Room")
public class RoomImpl extends AHRDFObjectImpl implements Room {
		
	@Iri(rdfs + "label")
	private String label;
	
	/*@Sparql("PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" + 
			"PREFIX ah:<http://purl.org/artsholland/1.0/>\n" +
			"SELECT DISTINCT ?room WHERE { \n" +
			"?room a <ah:Room> . \n" +
			"?room rdfs:label $label .}")
	public Room findRoomByLabel(@Bind("label") String label) {
		return null;
	}*/
	
	@Override
	public String getLabel() {
		return label;
	}
	
	
}
