package org.waag.ah.importer;

import java.net.URL;

public class ImportDocument {
	private URL url;
	private String data;
	public URL getUrl() {
		return url;
	}
	public String getData() {
		return data;
	}
	@Override
	public String toString() {
		return getData();
	}
}
