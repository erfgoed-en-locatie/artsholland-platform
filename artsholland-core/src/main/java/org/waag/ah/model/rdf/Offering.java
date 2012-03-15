package org.waag.ah.model.rdf;

import org.codehaus.jackson.annotate.JsonProperty;

public interface Offering extends AHRDFObject {

	@JsonProperty
	public String getName();
	
	@JsonProperty
	public UnitPriceSpecification getUnitPriceSpecification();

	@JsonProperty
	public String getDescription();
		
}
