package org.waag.ah.tinkerpop;

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
		final T nextItem = starts.next();
		final InputStreamFromOutputStream<String> isos = new InputStreamFromOutputStream<String>() {
			@Override
			public String produce(final OutputStream outStream)
					throws Exception {
				process(nextItem, outStream);
				return "Processed: "+nextItem.toString();
			}
		};
		return isos;
	}
}
