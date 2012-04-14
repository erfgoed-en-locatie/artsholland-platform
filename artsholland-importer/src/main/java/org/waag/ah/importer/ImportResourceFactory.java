package org.waag.ah.importer;

import java.net.URL;

import org.waag.ah.ImportResource;

/**
 * Respurce import factory.
 * 
 * @author Raoul Wissink <raoul@raoul.net>
 */
public class ImportResourceFactory {
	/**
	 * Import URL resource.
	 * 
	 * @param url
	 * @return ImportResource
	 *
	 * @author	Raoul Wissink <raoul@raoul.net>
	 * @since	Apr 3, 2012
	 */
	public static ImportResource getimportResource(URL url) {
		return new URLImportResource(url);
	}
}
