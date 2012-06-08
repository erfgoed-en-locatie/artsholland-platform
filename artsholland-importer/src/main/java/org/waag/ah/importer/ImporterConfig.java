package org.waag.ah.importer;

import org.joda.time.DateTime;

public class ImporterConfig {
	private String id;
	private ImportStrategy strategy;
	private DateTime importFrom;
	private DateTime importUntil;

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setStrategy(ImportStrategy strategy) {
		this.strategy = strategy;
	}
	
	public ImportStrategy getStrategy() {
		return this.strategy;
	}
	
	public void setFromImportDate(DateTime date) {
		this.importFrom = date;
	}
	
	public final DateTime getFromImportDate() {
		return importFrom;
	}
	
	public void setUntilImportDate(DateTime date) {
		this.importUntil = date;
	}
	
	public final DateTime getUntilImportDate() {
		return importUntil;
	}
}
