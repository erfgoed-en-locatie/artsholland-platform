package org.waag.ah.jackson;

/*
 * TODO: move to other package
 */
import java.io.IOException;

import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class XMLGregorianCalendarSerializer extends JsonSerializer<XMLGregorianCalendar> {

	@Override
	public void serialize(XMLGregorianCalendar value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException, JsonProcessingException {

		jgen.writeString(value.toString());
		
	}
	
	public Class<XMLGregorianCalendar> handledType() {
		return XMLGregorianCalendar.class; 
	}

}
