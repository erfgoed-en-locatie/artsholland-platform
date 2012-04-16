package org.waag.ah;

import java.util.List;

public interface ImportService {
	void importResource(List<ImportResource> resources, ImportMetadata metadata)
			throws Exception;
}
