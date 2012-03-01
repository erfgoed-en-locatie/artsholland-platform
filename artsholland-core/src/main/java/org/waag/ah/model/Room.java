package org.waag.ah.model;

import org.openrdf.annotations.Bind;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Sparql;

@Iri(Room.ah + "Room")
public interface Room {
		
	public static final String ah = "http://purl.org/artsholland/1.0/";
	public static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	@Sparql("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		  "SELECT DISTINCT ?room WHERE { \n" +
		  "?room a <http://purl.org/artsholland/1.0/Room> . \n" +
			"?room rdf:Label $label .}")
	public abstract Room findRoomByLabel(@Bind("label") String label);
	
	@Iri(rdf + "Label")
	public abstract String getLabel();

	public abstract void setLabel(String label);

	
}
