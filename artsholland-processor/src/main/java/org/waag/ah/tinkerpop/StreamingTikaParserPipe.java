package org.waag.ah.tinkerpop;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.waag.ah.tika.StreamingToRDFContentHandler;
import org.waag.ah.util.URLTools;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class StreamingTikaParserPipe extends AbstractStreamingPipe<URL> {
//	private static final Logger logger = LoggerFactory.getLogger(TikaParserPipe.class);
	private CharArrayWriter writer = new CharArrayWriter();
	
	@Override
	protected void process(URL parseUrl, ObjectOutputStream out)
			throws IOException, SAXException, TikaException {
		URL url = URLTools.getAuthenticatedUrl(parseUrl);
		URLConnection conn = url.openConnection();
		InputStream in = conn.getInputStream();
		
		try {
			AutoDetectParser parser = new AutoDetectParser();
			ContentHandler handler = new StreamingToRDFContentHandler(writer, out);
	
			Metadata metadata = new Metadata();
			metadata.add(Metadata.RESOURCE_NAME_KEY, url.toExternalForm());
			metadata.add(Metadata.CONTENT_ENCODING,
					new InputStreamReader(in).getEncoding());
	
			parser.parse(in, handler, metadata, new ParseContext());
		} catch(Exception e) {
			throw new TikaException(e.getMessage(), e);
		} finally {
			in.close();
			out.close();
		}
	}
}
