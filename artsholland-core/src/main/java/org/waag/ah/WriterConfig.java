package org.waag.ah;

import java.util.Map;

public interface WriterConfig {
	public String getFormat();
	public boolean isPrettyPrint();
	public String getLanguageTag();
	public Map<String, String> getMetaData();
	public String getMetaData(String key);
	public String getBaseUri();
	public boolean isWrapResults();
}
