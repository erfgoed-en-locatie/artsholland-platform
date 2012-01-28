package org.waag.ah;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface DocumentWriter {
	public void write(String message, Map<String, String> metadata) throws IOException;
	public void write(InputStream inputStream, Map<String, String> metadata) throws IOException;
}
