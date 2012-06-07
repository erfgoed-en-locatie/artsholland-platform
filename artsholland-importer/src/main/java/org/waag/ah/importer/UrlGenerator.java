package org.waag.ah.importer;

import java.net.URL;
import java.util.List;


public interface UrlGenerator {
	List<URL> getUrls(ImporterConfig config);
}
