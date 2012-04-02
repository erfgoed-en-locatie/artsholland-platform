package org.waag.ah.jackson;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.waag.ah.model.rdf.Offering;
import org.waag.ah.model.rdf.UnitPriceSpecification;

public class OfferingSerializer extends JsonSerializer<Offering> {

	@Override
	public void serialize(Offering value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException, JsonProcessingException {
		
		jgen.writeStartObject();
		
		jgen.writeStringField("name", value.getName());
		jgen.writeStringField("description", value.getName());
				
		UnitPriceSpecification price = value.getUnitPriceSpecification();
		
		jgen.writeStringField("priceCurrency", price.getHasCurrency());
		jgen.writeNumberField("lowPrice", price.getHasMinCurrencyValue());
		jgen.writeNumberField("highPrice", price.getHasMaxCurrencyValue());

		jgen.writeEndObject();
		
	}
	
	public Class<Offering> handledType() {
		return Offering.class; 
	}

}
