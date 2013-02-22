package org.waag.ah.tinkerpop;

import java.io.ObjectInputStream;

import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.pipes.AbstractPipe;

public class ObjectInputStreamReaderPipe extends AbstractPipe<ObjectInputStream, String> {
	private Logger logger = LoggerFactory.getLogger(ObjectInputStreamReaderPipe.class);
	
	private ObjectInputStream tempObjectInputStream;
	
	@Override
    public String processNextStart() {
        while (true) {
        	try {
                return (String) tempObjectInputStream.readObject();
        	} catch(Exception e) {
        		tempObjectInputStream = this.starts.next();
        		Throwable cause = e.getCause();
        		if (cause instanceof TikaException) {
        			throw new RuntimeException(cause);
        		}
//        		logger.warn("Exception reading ObjectInputStream: "+e);
        	}
        }
    }

	@Override
    public void reset() {
        tempObjectInputStream = null;
        super.reset();
    }
}
