package org.waag.ah.importer;

/**
 * @author Raoul Wissink <raoul@raoul.net>
 * @todo Merge with ImportConfig.
 */
public enum ImportStrategy {

	FULL,
	INCREMENTAL;

	public static ImportStrategy fromValue(String value) {
		if (value == null || "".equals(value)) {
			return null;
		}
		return valueOf(value.toUpperCase());
	}	
}
