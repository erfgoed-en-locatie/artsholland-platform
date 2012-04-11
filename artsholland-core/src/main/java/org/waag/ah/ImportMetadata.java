package org.waag.ah;

public class ImportMetadata {
	private String jobIdentifier;
	private String baseURI;

	public void setJobIdentifier(String jobIdentifier) {
		this.jobIdentifier = jobIdentifier;
	}
	
	public String getJobIdentifier() {
		return jobIdentifier;
	}

	public String getBaseURI() {
		return baseURI;
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}
}
