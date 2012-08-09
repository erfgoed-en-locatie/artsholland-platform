package org.waag.ah.spring.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.Period;
import org.waag.ah.model.DbObject;
import org.waag.ah.model.Import;
import org.waag.ah.model.Importer;

@SuppressWarnings("serial")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ImporterImpl implements Importer, Serializable, DbObject {

	private long id;
	private String name;
	private String title;
	private ImporterType type;
	private List<Import> imports;
	
	@Override
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public List<Import> getImports() {
		return imports;
	}
	
	public void setImports(List<Import> imports) {
		this.imports = imports;
	}

	@Override
	public ImporterType getType() {
		return type;
	}
	
	public void setType(ImporterType type) {
		this.type = type;
	}

	@Override
	public void truncate() {
		// TODO Auto-generated method stub		
	}

	@Override
	public void schedule(Period period) {
		// TODO Auto-generated method stub		
	}	

	
}
