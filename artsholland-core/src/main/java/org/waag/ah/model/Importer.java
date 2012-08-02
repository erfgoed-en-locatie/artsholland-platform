package org.waag.ah.model;

import java.util.List;

import org.joda.time.Period;

public interface Importer {
	
	public enum ImporterType {
    IMPORTER_TYPE_FULL,
    IMPORTER_TYPE_INCREMENTAL
	}
	
	public String getName();
	public String getTitle();
	public List<Import> getImports();
	public ImporterType getType();
	
	public void truncate();
	public void schedule(Period period);
	
}
