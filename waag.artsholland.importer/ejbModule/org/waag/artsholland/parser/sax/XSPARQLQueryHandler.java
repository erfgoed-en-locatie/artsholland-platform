package org.waag.artsholland.parser.sax;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XdmItem;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToXMLContentHandler;
import org.deri.xsparql.rewriter.XSPARQLProcessor;
import org.waag.artsholland.parser.rdf.TurtleParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XSPARQLQueryHandler extends ContentHandlerDecorator {
	private XQueryEvaluator evaluator;
	private String rootElement;
	private ToXMLContentHandler xmlCollector;
	private ContentHandler handler;
	private TurtleParser turtleParser;
	private ParseContext context;
	private String data;

	public XSPARQLQueryHandler(ContentHandler handler, Metadata metadata, 
			ParseContext context, String query)	throws TikaException {
		super(handler);
		this.context = context; 
		this.handler = handler; 
		this.turtleParser = new TurtleParser();
		try {
			XSPARQLProcessor xp = new XSPARQLProcessor();
			String q = xp.process(new StringReader(query));
			Configuration config = new Configuration();
			Processor processor = new Processor(config);
			XQueryCompiler compiler = processor.newXQueryCompiler();
			evaluator = compiler.compile(q).load();	
		} catch (Exception e) {
			throw new TikaException(e.getMessage(), e);
		}			
	}

	private boolean currentNode() {
		return xmlCollector != null;
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		// We want to match each root element against the XSPARQL query.
		if (rootElement == null) {
			rootElement = localName;
		}
		
		// Start collecting characters when we encounter our root element.
		if (localName.equals(rootElement)) {
			xmlCollector = new ToXMLContentHandler();
			xmlCollector.startDocument();
		}
		if (currentNode()) {
			xmlCollector.startElement(uri, localName, qName, attributes);
		}				
	}
    
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (currentNode()) {
			xmlCollector.characters(ch, start, length);
		}			
	}
//    protected void write(String string) throws SAXException {
//        super.characters(string.toCharArray(), 0, string.length());
//    }

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (currentNode()) {
			xmlCollector.endElement(uri, localName, qName);
		}
		if (localName.equals(rootElement)) {	
			xmlCollector.endDocument();
			StreamSource xml = new StreamSource(
					new StringReader(xmlCollector.toString()));	
			
//			System.out.println(xmlCollector.toString());
			try {
				evaluator.setSource(xml);
				StringBuilder combined = new StringBuilder();
				for (XdmItem item : evaluator) {
//					System.out.println(item);
					combined.append(item);
				}
				
				Metadata metadata = new Metadata();
				metadata.set(Metadata.CONTENT_TYPE, "text/turtle");
            
				// Handler stringbuffer fills up, causing an OutOfMemoryError.
				data = combined.toString();
//				System.out.println(data);
				turtleParser.parse(
						new ByteArrayInputStream(combined.toString().getBytes()), 
						handler, metadata, context);

			} catch (SAXParseException e) {
				System.out.println(xmlCollector.toString());
				System.out.println(data);
				throw e;
			} catch (SaxonApiException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TikaException e) {
				e.printStackTrace();
			} finally {
				xmlCollector = null;
			}
		}
	}
}
