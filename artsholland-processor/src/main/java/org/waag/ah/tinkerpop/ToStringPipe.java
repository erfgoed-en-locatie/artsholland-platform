package org.waag.ah.tinkerpop;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.collections.MapUtils;

import com.tinkerpop.pipes.AbstractPipe;

public class ToStringPipe extends AbstractPipe<Object, String> {

	@SuppressWarnings("rawtypes")
	@Override
	protected String processNextStart() throws NoSuchElementException {
		Object result = this.starts.next();
		
		try {
			if (result instanceof Map) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);			
				MapUtils.debugPrint(ps, null, (Map) result);
				return baos.toString("UTF-8");
			} else {
				return result.toString();
			}
		} catch (UnsupportedEncodingException e) {
			throw new NoSuchElementException(e.getMessage());
		}
	}
}
