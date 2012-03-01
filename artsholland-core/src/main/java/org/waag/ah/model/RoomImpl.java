package org.waag.ah.model;

import org.openrdf.annotations.Iri;


@Iri(RoomImpl.ah + "Room")
public class RoomImpl {
		
	public static final String ah = "http://purl.org/artsholland/1.0/";
	public static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	@Iri(rdf + "Label")
	private String label;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	
}
