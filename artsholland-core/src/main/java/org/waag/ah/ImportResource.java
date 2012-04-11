package org.waag.ah;

import java.io.IOException;
import java.io.InputStream;

public interface ImportResource {
	public InputStream parse() throws IOException;
}
