
package org.waag.ah.model.rdf;

import org.openrdf.annotations.Iri;

@Iri(RDFObject.ah + "Room")
public abstract class RoomImpl extends RDFObject implements Room {
		
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
	
	/*@Override
	public void setLabel(String label) {
		this.label = label;
	}*/

	
	
}
