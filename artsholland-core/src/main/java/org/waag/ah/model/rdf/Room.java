package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public interface Room extends org.openrdf.repository.object.RDFObject{

	/*@Sparql("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		  "SELECT DISTINCT ?room WHERE { \n" +
		  "?room a <http://purl.org/artsholland/1.0/Room> . \n" +
			"?room rdf:Label $label .}")
	public abstract Room findRoomByLabel(@Bind("label") String label);*/
	
	//public abstract Room findRoomByLabel(String label);
	
	@JsonProperty
	public abstract String getLabel();
	
}
