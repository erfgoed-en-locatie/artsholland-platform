package org.waag.ah.tinkerpop.pipe;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import com.gc.iotools.stream.is.InputStreamFromOutputStream;
import com.tinkerpop.pipes.AbstractPipe;

public abstract class AbstractStreamingPipe<T> extends
		AbstractPipe<T, InputStream> {

	protected abstract void process(T in, OutputStream out) throws Exception;

	@Override
	public final InputStream processNextStart() throws NoSuchElementException {
		final InputStreamFromOutputStream<String> isos = new InputStreamFromOutputStream<String>() {
			@Override
			public String produce(final OutputStream outStream)
					throws Exception {
				process(starts.next(), outStream);
				return "OK";
			}
		};
		return isos;
	}
}
