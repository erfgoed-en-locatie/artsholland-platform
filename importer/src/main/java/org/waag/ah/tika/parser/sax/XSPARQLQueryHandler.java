package org.waag.ah.tika.parser.sax;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XdmItem;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.deri.xsparql.rewriter.XSPARQLProcessor;
import org.openrdf.model.vocabulary.RDF;
import org.waag.ah.tika.parser.rdf.TurtleParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XSPARQLQueryHandler extends ContentHandlerDecorator {
//	private Logger logger = Logger.getLogger(XSPARQLQueryHandler.class);
	private XQueryEvaluator evaluator;
	private String rootElement;
	private ToXMLContentHandler xmlCollector;
//	private ContentHandler handler;
	private TurtleParser turtleParser;
	private ParseContext context;
	private Metadata metadata;
	private NamespaceCollector namepool;
	private ContentHandler handler;
	private Matcher matcher;

	public XSPARQLQueryHandler(ContentHandler handler, Metadata metadata, 
			ParseContext context, String query, String rootElement)	throws TikaException {
//		super(handler);
		this.matcher = new XPathParser("rdf", RDF.NAMESPACE).parse("/rdf:RDF/descendant::node()");
		this.handler = handler;
		super.setContentHandler(this.handler);
		this.context = context;
		this.metadata = metadata; 
		this.turtleParser = new TurtleParser();
		this.rootElement = rootElement;
		try {
			XSPARQLProcessor xp = new XSPARQLProcessor();			
			String q = xp.process(new StringReader(query));
			Configuration config = new Configuration();
			namepool = new NamespaceCollector();
			config.setNamePool(namepool);
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
//		System.out.println("START");
		this.handler.startDocument();
		AttributesImpl atts = new AttributesImpl();
		for (Entry<String, String> mapping : namepool.getMappings().entrySet()) {
			this.handler.startPrefixMapping(mapping.getKey(), mapping.getValue());
//			System.out.println(mapping.getKey()+" : "+mapping.getValue());
		}
		this.handler.startElement(RDF.NAMESPACE, "RDF", "rdf:RDF", atts);
	}

	@Override
	public void endDocument() throws SAXException {
		super.endElement(RDF.NAMESPACE, "RDF", "rdf:RDF");
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		/*if (rootElement == null) {
			rootElement = localName;
		}*/
		
		// Start collecting characters when we encounter our root element.
		if (localName.equals(rootElement)) {
			xmlCollector = new ToXMLContentHandler();
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

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (currentNode()) {
			xmlCollector.endElement(uri, localName, qName);
		}
		if (localName.equals(rootElement)) {
			
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
				
				Metadata mdata = new Metadata();
				mdata.set(Metadata.CONTENT_TYPE, "text/turtle");
				mdata.set(Metadata.RESOURCE_NAME_KEY, metadata.get(Metadata.RESOURCE_NAME_KEY));
            
				turtleParser.parse(
						new ByteArrayInputStream(combined.toString().getBytes()), 
						new MatchingContentHandler(
						new EmbeddedContentHandler(this.handler), matcher), mdata, context);
				
//				handler.endDocument();
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
	
//	private static class EmbeddedRDFHandler extends EmbeddedContentHandler {
//		private boolean started = false;
//
//		public EmbeddedRDFHandler(ContentHandler handler) {
//			super(handler);
//			// TODO Auto-generated constructor stub
//		}
//
//		@Override
//		public void startElement(String uri, String localName, String name,
//				Attributes atts) throws SAXException {
//			if (name != "rdf:RDF") {
//				super.startElement(uri, localName, name, atts);
//			} else {
//				started = true;
//				System.out.println("START");
//			}
//		}
//
//		@Override
//		public void endElement(String uri, String localName, String name)
//				throws SAXException {
//			if (name != "rdf:RDF") {
//				super.endElement(uri, localName, name);
//			}
//		}
//
//		@Override
//		public void startPrefixMapping(String prefix, String uri)
//				throws SAXException {
////			if (!started) {
////				System.out.println(prefix+":"+uri);
////			}
//			super.startPrefixMapping(prefix, uri);
//		}
//
////		@Override
////		public void startDocument() throws SAXException {
////			if (!started) {
////				System.out.println("STARTING");
////				super.startDocument();
////				started = true;
////			}
////		}
//
////		@Override
////		public void endDocument() throws SAXException {
////			// TODO Auto-generated method stub
////			super.endDocument();
////		}
////		public EmbeddedRDFHandler(ContentHandler handler) {
////			super(handler);
////		}
//
//		
//		
//	}
	
	private static class NamespaceCollector extends NamePool {
		private static final long serialVersionUID = -2352244560755994347L;
		private Map<String, String> mappings = new HashMap<String, String>();
		
		@Override
		public synchronized int allocateNamespaceCode(String prefix, String uri) {
			if (!prefix.startsWith("_")) {
				mappings.put(prefix, uri);
			}
			return super.allocateNamespaceCode(prefix, uri);
		}
		
		public Map<String, String> getMappings() {
			return mappings;
		}
	}
}
