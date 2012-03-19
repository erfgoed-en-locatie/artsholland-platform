package org.waag.ah.jackson;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.waag.ah.model.rdf.AHRDFObject;

public class AHRDFObjectSerializer extends JsonSerializer<AHRDFObject> {

	@Override
	public void serialize(AHRDFObject value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException, JsonProcessingException {

		jgen.writeString(value.getLabel());
		
	}
	
	public Class<AHRDFObject> handledType() {
		return AHRDFObject.class; 
	}

}
