package org.waag.ah.tinkerpop;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.waag.ah.zeromq.Parser.Document;

import com.tinkerpop.pipes.AbstractPipe;

public class TikaParserPipe extends AbstractPipe<Document, String> {
	AutoDetectParser parser = new AutoDetectParser();
	@Override
	protected String processNextStart() throws NoSuchElementException {
		final Document doc = starts.next();
		final String data = doc.toString();
		try {
			InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));
			try {
				ToXMLContentHandler handler = new ToXMLContentHandler();
				Metadata metadata = new Metadata();
				
				metadata.add(Metadata.RESOURCE_NAME_KEY, doc.getUrl().toExternalForm());
				metadata.add(Metadata.CONTENT_ENCODING, new InputStreamReader(is).getEncoding());
				
				parser.parse(is, handler, metadata); //, new ParseContext()
				handler.endDocument();
				
				return handler.toString();
			} finally {
				is.close();
			}
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
