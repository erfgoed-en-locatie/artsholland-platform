package org.waag.ah.service;

import java.util.List;

import org.waag.ah.ImportMetadata;
import org.waag.ah.ImportResource;

public interface ImportService {
	void importResource(List<ImportResource> resources, ImportMetadata metadata)
			throws Exception;
}
