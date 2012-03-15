package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonProperty;

public interface Room extends AHRDFObject {

	/*@Sparql("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		  "SELECT DISTINCT ?room WHERE { \n" +
		  "?room a <http://purl.org/artsholland/1.0/Room> . \n" +
			"?room rdf:Label $label .}")
	public abstract Room findRoomByLabel(@Bind("label") String label);*/
	
	//public abstract Room findRoomByLabel(String label);
	
	@JsonProperty
	public String getLabel();
	
}
