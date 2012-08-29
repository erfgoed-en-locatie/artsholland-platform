package org.waag.ah.spring.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.waag.ah.UUIDGenerator;
import org.waag.ah.model.DbObject;
import org.waag.ah.model.PrivateObject;

@SuppressWarnings("serial")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PrivateObjectImpl implements PrivateObject, Serializable, DbObject<String> {

	String id = UUIDGenerator.generate();
	private String uri;
	private Object data;
	
	@Override
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getUri() {
		return uri.toString();
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
}
