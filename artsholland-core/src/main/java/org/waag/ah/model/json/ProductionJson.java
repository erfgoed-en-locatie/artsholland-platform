package org.waag.ah.model.json;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonProperty;
import org.waag.ah.model.rdf.Genre;
import org.waag.ah.model.rdf.ProductionType;

public abstract class ProductionJson extends AHRDFObjectJson {
	
//	ah:cidn 
//	ah:genre 
//	ah:party 
//	ah:people 
//	ah:productionType
//	dc:description 
//  ah:shortDescription	
//	rdf:type
//	owl:sameAs
//	foaf:homepage
	
	@JsonProperty()
	public abstract String getURI();
	
	@JsonProperty
	public abstract String getCidn();
	
	@JsonProperty
	public abstract String getTitle();
	
	@JsonProperty
	public abstract String getDescription();
	
	@JsonProperty
	public abstract String getShortDescription();
	
	@JsonProperty
	public abstract String getHomepage();

	@JsonProperty	
	public abstract String getParty();
	
	@JsonProperty
	public abstract String getPeople();

	@JsonProperty
	public abstract Genre getGenre();	
	
	@JsonProperty
	public abstract ProductionType getProductionType();	
	
	@JsonProperty
	public abstract Set<String> getTags();
		
}
