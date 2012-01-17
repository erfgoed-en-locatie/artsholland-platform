package org.waag.ah.service.importer;

import java.io.Closeable;
import java.io.IOException;

import org.apache.tika.metadata.Metadata;

public interface DocumentWriter extends Closeable {
	public void write(String message, Metadata metadata) throws IOException;
}
