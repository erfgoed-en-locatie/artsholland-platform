package org.waag.ah.tinkerpop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import com.gc.iotools.stream.is.InputStreamFromOutputStream;
import com.tinkerpop.pipes.AbstractPipe;

public abstract class AbstractStreamingPipe<T> extends
		AbstractPipe<T, ObjectInputStream> {

	protected abstract void process(T in, ObjectOutputStream out) throws Exception;

	@Override
	public final ObjectInputStream processNextStart() throws NoSuchElementException {
		final T nextItem = starts.next();
		final InputStreamFromOutputStream<String> isos = new InputStreamFromOutputStream<String>() {
			@Override
			public String produce(final OutputStream outStream)
					throws Exception {
				process(nextItem, new ObjectOutputStream(outStream));
				return "Processed: "+nextItem.toString();
			}
		};
		try {
			return new ObjectInputStream(isos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
