package org.waag.ah.jackson;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.waag.ah.model.decorator.EventJsonDecorator;
import org.waag.ah.model.decorator.ProductionJsonDecorator;
import org.waag.ah.model.json.*;
import org.waag.ah.model.rdf.*;

public class AHObjectMapper extends ObjectMapper {

	public AHObjectMapper() {
		super();
		addMixInAnnotations();
		addSerializers();
	}

	private void addMixInAnnotations() {
		getSerializationConfig().addMixInAnnotations(EventJsonDecorator.class, EventJson.class);
		getSerializationConfig().addMixInAnnotations(ProductionJsonDecorator.class, ProductionJson.class);
		
		getSerializationConfig().addMixInAnnotations(Room.class, RoomJson.class);
		getSerializationConfig().addMixInAnnotations(Venue.class, VenueJson.class);
		getSerializationConfig().addMixInAnnotations(Offering.class, OfferingJson.class);
		getSerializationConfig().addMixInAnnotations(UnitPriceSpecification.class, UnitPriceSpecificationJson.class);		
		getSerializationConfig().addMixInAnnotations(Attachment.class, AttachmentJson.class);
		getSerializationConfig().addMixInAnnotations(AttachmentType.class, AttachmentTypeJson.class);
		getSerializationConfig().addMixInAnnotations(EventStatus.class, EventStatusJson.class);
		getSerializationConfig().addMixInAnnotations(EventType.class, EventTypeJson.class);
		getSerializationConfig().addMixInAnnotations(Genre.class, GenreJson.class);
		getSerializationConfig().addMixInAnnotations(ProductionType.class, ProductionTypeJson.class);
		getSerializationConfig().addMixInAnnotations(VenueType.class, VenueTypeJson.class);
	}

	private void addSerializers() {
		SimpleModule simpleModule = new SimpleModule("AHRDFModule", new Version(1,0, 0, null));

		//simpleModule.addSerializer(new LangStringSerializer());
		simpleModule.addSerializer(new XMLGregorianCalendarSerializer());
		simpleModule.addSerializer(new OfferingSerializer());

		simpleModule.addSerializer(EventType.class, new AHRDFObjectSerializer());
		simpleModule.addSerializer(EventStatus.class, new AHRDFObjectSerializer());
		simpleModule.addSerializer(Room.class, new AHRDFObjectSerializer());
		simpleModule.addSerializer(AttachmentType.class, new AHRDFObjectSerializer());
		simpleModule.addSerializer(ProductionType.class, new AHRDFObjectSerializer());
		simpleModule.addSerializer(VenueType.class, new AHRDFObjectSerializer());
		simpleModule.addSerializer(Genre.class, new AHRDFObjectSerializer());
		
		registerModule(simpleModule);
	}

}
