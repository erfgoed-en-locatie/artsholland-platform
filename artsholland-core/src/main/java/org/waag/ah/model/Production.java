package org.waag.ah.model;

import org.openrdf.annotations.Iri;

//import org.openrdf.repository.object.annotations.iri;

@Iri(Production.NS + "Production")
public class Production {
		
	public static final String NS = "http://purl.org/artsholland/1.0/";

  //String title;

	
	//private String cidn;
	
	//private String party;
	//private String people;
	
	
	@Iri(NS + "shortDescription") 
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
		
}
