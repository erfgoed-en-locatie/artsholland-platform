package org.waag.ah.model;

import java.io.Serializable;

public interface DbObject<ID extends Serializable> {

	public ID getId();
	
}
