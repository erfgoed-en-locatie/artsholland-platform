package org.waag.ah.jackson;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.openrdf.repository.object.LangString;

public class LangStringSerializer extends JsonSerializer<LangString> {

	@Override
	public void serialize(LangString value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException, JsonProcessingException {

		jgen.writeStartObject();
		jgen.writeStringField(value.getLang(), value.toString());
		jgen.writeEndObject();
	}
	
	public Class<LangString> handledType() {
		return LangString.class; 
	}

}
