package org.waag.ah.tika;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToTextContentHandler;
import org.waag.ah.importer.ImportDocument;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DocumentParser {
//	private Logger logger = LoggerFactory.getLogger(DocumentParser.class);
	private AutoDetectParser parser = new AutoDetectParser();
	public String parse(ImportDocument doc) throws IOException, SAXException, TikaException {
		String data = doc.getData();
		InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));
		try {
			ContentHandler handler = new ToTextContentHandler();
			Metadata metadata = new Metadata();
			
			metadata.add(Metadata.RESOURCE_NAME_KEY, doc.getUrl().toExternalForm());
			metadata.add(Metadata.CONTENT_ENCODING, new InputStreamReader(is).getEncoding());
			
			parser.parse(is, handler, metadata); //, new ParseContext()
			handler.endDocument();
			
			return handler.toString();
		} finally {
			is.close();
		}
	}
}
