package org.waag.ah.tinkerpop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.waag.ah.tika.ToRDFContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaParserPipe extends AbstractStreamingPipe<URL> {
//	private static final Logger logger = LoggerFactory.getLogger(TikaParserPipe.class);

	private ByteArrayOutputStream writer = new ByteArrayOutputStream();
	
	@Override
	protected void process(URL url, ObjectOutputStream out)
			throws IOException, SAXException, TikaException {
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
		} finally {
			in.close();
			out.close();
		}
//		return in.getResult();
	}
	
	@Override
	public void reset() {
		writer = null;
		super.reset();
	}
	
	private class StreamingToRDFContentHandler extends ToRDFContentHandler {
		private ObjectOutputStream outputStream;

		public StreamingToRDFContentHandler(OutputStream stream, ObjectOutputStream outputStream)
				throws UnsupportedEncodingException {
			super(stream, "UTF-8");
			this.outputStream = outputStream;
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
			try {
				outputStream.writeUTF(writer.toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				writer.reset();
			}
		}
	}
}
