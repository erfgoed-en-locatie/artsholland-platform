package org.waag.ah.tika;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import org.openrdf.model.vocabulary.RDF;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class StreamingToRDFContentHandler extends ToXMLContentHandler {
	private final Map<String, String> namespaces = new HashMap<String, String>();
	private Stack<String> stack = new Stack<String>();
	private CharArrayWriter writer;
	private ObjectOutputStream outputStream;

	public StreamingToRDFContentHandler(CharArrayWriter writer, ObjectOutputStream outputStream)
			throws UnsupportedEncodingException {
		super(writer);
		this.writer = writer;
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