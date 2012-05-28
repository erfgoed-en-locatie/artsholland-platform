package org.waag.ah.tinkerpop.pipes;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;

import com.tinkerpop.pipes.AbstractPipe;

public class StreamReaderPipe extends AbstractPipe<InputStream, String> {

	@Override
	protected String processNextStart() throws NoSuchElementException {
		try {
			InputStream in = this.starts.next();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer);
			in.close();
			return writer.toString();
		} catch (IOException e) {
			throw new NoSuchElementException(e.getMessage());
		}
	}
}
