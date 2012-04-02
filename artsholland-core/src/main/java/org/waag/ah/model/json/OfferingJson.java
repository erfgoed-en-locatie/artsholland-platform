package org.waag.ah.model.json;

import org.codehaus.jackson.annotate.JsonProperty;

public abstract class OfferingJson extends AHRDFObjectJson {

	@JsonProperty
	public abstract String getName();
	
	@JsonProperty
	public abstract UnitPriceSpecificationJson getUnitPriceSpecification();

	@JsonProperty
	public abstract String getDescription();
		
}
