package org.waag.ah.importer;

import org.joda.time.DateTime;
import org.openrdf.model.URI;

public class ImportConfig {
	private String id;
	private ImportStrategy strategy = ImportStrategy.FULL;
	private DateTime importFrom;
	private DateTime importUntil;
	private URI context;
	
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
	
	public void setFromDateTime(DateTime date) {
		this.importFrom = date;
	}
	
	public final DateTime getFromDateTime() {
		return importFrom;
	}
	
	public void setToDateTime(DateTime date) {
		this.importUntil = date;
	}
	
	public final DateTime getToDateTime() {
		return importUntil;
	}

	public void setContext(URI context) {
		this.context = context;
	}
	
	public URI getContext() {
		return this.context;
	}
}
