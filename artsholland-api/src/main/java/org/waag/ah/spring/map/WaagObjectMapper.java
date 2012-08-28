package org.waag.ah.spring.map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

public class WaagObjectMapper extends ObjectMapper {
	
	 public WaagObjectMapper() {        
        super.configure(Feature.WRITE_DATES_AS_TIMESTAMPS, false);       
    }
}
