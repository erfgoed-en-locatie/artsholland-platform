package org.waag.ah.tinkerpop;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.openrdf.model.vocabulary.RDF;
import org.waag.ah.PasswordAuthenticator;
import org.waag.ah.tika.ToXMLContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class TikaParserPipe extends AbstractStreamingPipe<URL> {
//	private static final Logger logger = LoggerFactory.getLogger(TikaParserPipe.class);

	private CharArrayWriter writer = new CharArrayWriter();
	
	@Override
	protected void process(URL url, ObjectOutputStream out)
			throws IOException, SAXException, TikaException {
		
		boolean authenticated = false;
		String username = "";
		String password = "";
		
		String userInfo = url.getUserInfo();
		if (userInfo != null) {
			String[] usernamePassword = userInfo.split(":");
			if (usernamePassword.length == 2) {
				username = usernamePassword[0];
				password = usernamePassword[1];
				authenticated = true;
			}
		}		
		
		if (authenticated) {
			Authenticator.setDefault(new PasswordAuthenticator(username, password));
		} else {
			Authenticator.setDefault(null);
		}
		
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
//		return in.getResult();
	}
	
	private class StreamingToRDFContentHandler extends ToXMLContentHandler {
		private final Map<String, String> namespaces = new HashMap<String, String>();
		protected Stack<String> stack = new Stack<String>();
		private ObjectOutputStream outputStream;

		public StreamingToRDFContentHandler(CharArrayWriter writer, ObjectOutputStream outputStream)
				throws UnsupportedEncodingException {
			super(writer);
			this.outputStream = outputStream;
		}
	    
		@Override
		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			if (!namespaces.containsKey(prefix)) {
				namespaces.put(prefix, uri);
			}
		}

		@Override
		public void startDocument() throws SAXException {
		}

		@Override
		public void endDocument() throws SAXException {
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			if (stack.size() == 0) {
				for (Entry<String, String> ns : namespaces.entrySet()) {
					super.startPrefixMapping(ns.getKey(), ns.getValue());
				}
				super.startPrefixMapping("rdf", RDF.NAMESPACE);
				super.startPrefixMapping("xml","http://www.w3.org/XML/1998/namespace");
				super.startElement(RDF.NAMESPACE, "RDF", "rdf:RDF", new AttributesImpl());
			}
			super.startElement(uri, localName, qName, atts);
			stack.push(qName);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			super.endElement(uri, localName, qName);
			stack.pop();
			if (stack.size() == 0) {
				super.endElement(RDF.NAMESPACE, "RDF", "rdf:RDF");
				try {
					String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
					data += writer.toString();
					writer.reset();
					outputStream.writeObject(data);
				} catch (IOException e) {
					throw new SAXException(e);
				} finally {
				}
			}
		}
	}
}
