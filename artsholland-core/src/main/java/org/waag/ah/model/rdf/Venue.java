package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.openrdf.annotations.Iri;

@Iri(Venue.ah + "Venue")
@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public interface Venue {

	public static final String ah = "http://purl.org/artsholland/1.0/";
	public static final String rdfs="http://www.w3.org/2000/01/rdf-schema#";


}
