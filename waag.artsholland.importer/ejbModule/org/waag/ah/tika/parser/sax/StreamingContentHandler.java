package org.waag.ah.tika.parser.sax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToXMLContentHandler;
import org.waag.ah.DocumentWriter;
import org.waag.ah.jms.Properties;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class StreamingContentHandler extends ContentHandlerDecorator {
	private Logger logger = Logger.getLogger(StreamingContentHandler.class);
	private ByteArrayOutputStream stream;
	private ContentHandler handler;
	private long startTime;
	private long recordCount;
	private DocumentWriter writer;
	private Map<String, String> documentProps;

	public StreamingContentHandler(DocumentWriter writer, Metadata metadata) {
		super();
		
		this.writer = writer;
		this.stream = new ByteArrayOutputStream();
		
		this.documentProps = new HashMap<String, String>();
		this.documentProps.put(Properties.SOURCE_URL, 
				metadata.get(Metadata.RESOURCE_NAME_KEY));
		this.documentProps.put(Properties.CONTENT_TYPE, 
				metadata.get(Metadata.CONTENT_TYPE));
		this.documentProps.put(Properties.CHARSET, 
				metadata.get(Metadata.CONTENT_ENCODING));
		
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
			writer.write(stream.toString(), documentProps);
		} catch (IOException e) {
			throw new SAXException("Could not write document", e);
		}
		logger.debug(String.format("%6d%8d: %d", recordCount, 
				new Date().getTime()-startTime, stream.size()));
	}
}
