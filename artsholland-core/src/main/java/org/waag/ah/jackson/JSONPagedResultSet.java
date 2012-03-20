package org.waag.ah.jackson;

import java.util.LinkedHashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.waag.ah.model.rdf.AHRDFObject;

@JsonAutoDetect(fieldVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
@JsonSerialize(include=Inclusion.NON_EMPTY)
public class JSONPagedResultSet extends ObjectMapper {

	@JsonProperty
	long offset = 0;
	
	@JsonProperty
	long count = 0;
	
	@JsonProperty
	long total = 0;
	
	@JsonProperty
	@JsonSerialize(include=Inclusion.ALWAYS)
	Set<AHRDFObject> results;
		
	public JSONPagedResultSet(AHRDFObject result) {
			
		results = new LinkedHashSet<AHRDFObject>();
		if (result instanceof AHRDFObject) {
			results.add(result);
			this.offset = 0;
			this.count = 1;
			this.total = 1;
		}				

	}
	
	public JSONPagedResultSet(Set<AHRDFObject> results, long offset, long total) {
		
		this.results = results;
		this.offset = offset;
		this.count = results.size();
		this.total = total;
		
	}

}
