package org.waag.artsholland.parser.sax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;

import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class StreamingContentHandler extends ContentHandlerDecorator {
	private ByteArrayOutputStream stream;
	private ToXMLContentHandler handler;
	private long startTime;
	private long recordCount;
	private Writer writer;

	public StreamingContentHandler(Writer writer) {
		super();
		this.writer = writer;
		this.stream = new ByteArrayOutputStream();
		try {
			handler = new ToXMLContentHandler(stream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		super.setContentHandler(handler);
	}
    
	public StreamingContentHandler(OutputStream stream) {
        this(new OutputStreamWriter(stream));
    }
    
	protected StreamingContentHandler(ContentHandler handler) {
		super(handler);
	}
	
	@Override
	public void startDocument() throws SAXException {
		stream.reset();
		recordCount++;
		super.startDocument();
		if (startTime == 0) {
			startTime = new Date().getTime();
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		try {
			writer.write(stream.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
//		String diff = String.format("%6d%6d", recordCount, new Date().getTime()-startTime);
//		System.out.println(diff+": "+stream.size());
//		System.out.println(buffer.toString());
//		System.out.println(stream.toString());
	}
}
