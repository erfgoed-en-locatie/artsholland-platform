package org.waag.ah.tinkerpop.pipes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.NoSuchElementException;

import com.tinkerpop.pipes.AbstractPipe;

/**
 * @author Raoul Wissink <raoul@raoul.net>
 *
 */
public class FetchURLPipe extends AbstractPipe<URL, InputStream> {

	@Override
	protected InputStream processNextStart() throws NoSuchElementException {
	    URL url = this.starts.next();
		try {
			URLConnection conn = url.openConnection();
			return conn.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
