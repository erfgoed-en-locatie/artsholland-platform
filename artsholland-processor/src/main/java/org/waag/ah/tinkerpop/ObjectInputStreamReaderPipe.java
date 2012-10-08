package org.waag.ah.tinkerpop;

import java.io.ObjectInputStream;

import com.tinkerpop.pipes.AbstractPipe;

public class ObjectInputStreamReaderPipe extends AbstractPipe<ObjectInputStream, String> {
	private ObjectInputStream tempObjectInputStream;
	
	@Override
    public String processNextStart() {
        while (true) {
        	try {
                return tempObjectInputStream.readUTF();
        	} catch (Exception e) {
        		tempObjectInputStream = this.starts.next();
        	}
        }
    }

	@Override
    public void reset() {
        tempObjectInputStream = null;
        super.reset();
    }
}
