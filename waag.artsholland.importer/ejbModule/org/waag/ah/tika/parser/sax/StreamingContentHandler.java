package org.waag.ah.tika.parser.sax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToXMLContentHandler;
import org.waag.ah.service.importer.DocumentWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class StreamingContentHandler extends ContentHandlerDecorator {
	private Logger logger = Logger.getLogger(StreamingContentHandler.class);
	private ByteArrayOutputStream stream;
	private ContentHandler handler;
	private long startTime;
	private long recordCount;
	private DocumentWriter writer;
	private Metadata metadata;

	public StreamingContentHandler(DocumentWriter writer, Metadata metadata) {
		super();
		this.writer = writer;
		this.metadata = metadata;
		this.stream = new ByteArrayOutputStream();
		try {
			handler = new ToXMLContentHandler(stream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		super.setContentHandler(handler);
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
			writer.write(stream.toString(), metadata);
		} catch (IOException e) {
			throw new SAXException("Could not write document", e);
		}
		logger.debug(String.format("%6d%8d: %d", recordCount, 
				new Date().getTime()-startTime, stream.size()));
	}
}
