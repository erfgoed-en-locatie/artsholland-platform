package org.waag.ah.tinkerpop;

import java.io.ObjectInputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.model.Statement;

import com.tinkerpop.pipes.AbstractPipe;
import com.tinkerpop.pipes.transform.ScatterPipe;
import com.tinkerpop.pipes.util.Pipeline;

public class ParserPipeline extends Pipeline<URL, Statement> {

	public ParserPipeline() throws ConfigurationException {
		super();
		this.addPipe(new TikaParserPipe());
		this.addPipe(new ObjectInputStreamReaderPipe());
		this.addPipe(new StatementGeneratorPipe());
		this.addPipe(new ScatterPipe<List<Statement>, Statement>());
	}
	
	private static class ObjectInputStreamReaderPipe extends AbstractPipe<ObjectInputStream, String> {
		private ObjectInputStream tempObjectInputStream;
		
	    public String processNextStart() {
	        while (true) {
	        	try {
	                return tempObjectInputStream.readUTF();
	        	} catch (Exception e) {
	        		tempObjectInputStream = this.starts.next();
	        	}
	        }
	    }

	    public void reset() {
	        tempObjectInputStream = null;
	        super.reset();
	    }
	}	
}
