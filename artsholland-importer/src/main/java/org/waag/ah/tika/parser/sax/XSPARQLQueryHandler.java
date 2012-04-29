package org.waag.ah.tika.parser.sax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XdmItem;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.deri.xsparql.XSPARQLProcessor;
import org.openrdf.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.XSPARQLCharacterEncoder;
import org.waag.ah.exception.ParserException;
import org.waag.ah.tika.parser.rdf.TurtleParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XSPARQLQueryHandler extends ContentHandlerDecorator {
	private Logger logger = LoggerFactory.getLogger(XSPARQLQueryHandler.class);
	private XQueryEvaluator evaluator;
	private ToXMLContentHandler xmlCollector;
	private TurtleParser turtleParser;
	private ParseContext context;
	private Metadata metadata;
	private NamespaceCollector namepool;
	private ContentHandler handler;
	private Matcher matcher;
	private Stack<String> stack;

	public XSPARQLQueryHandler(ContentHandler handler, Metadata metadata,
			ParseContext context, InputStream xquery)
			throws ParserException {
		this.matcher = new XPathParser("rdf", RDF.NAMESPACE).parse("/rdf:RDF/descendant::node()");
		this.handler = handler;
		super.setContentHandler(this.handler);
		this.context = context;
		this.metadata = metadata; 
		this.turtleParser = new TurtleParser();
		this.namepool = new NamespaceCollector();
		this.stack = new Stack<String>();
		
		try {
			XSPARQLProcessor xp = new XSPARQLProcessor();			
			String q = xp.process(xquery);//.process(new StringReader(query));
			Configuration config = new Configuration();
			config.setNamePool(namepool);
			Processor processor = new Processor(config);
			XQueryCompiler compiler = processor.newXQueryCompiler();			
			evaluator = compiler.compile(q).load();	
		} catch (Exception e) {
			throw new ParserException(e.getMessage());
		}			
	}

	private boolean processing() {
		return stack.size() > 0;
	}

	@Override
	public void startDocument() throws SAXException {
		this.handler.startDocument();
		AttributesImpl atts = new AttributesImpl();
		for (Entry<String, String> mapping : namepool.getMappings().entrySet()) {
			this.handler.startPrefixMapping(mapping.getKey(), mapping.getValue());
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
		if (!processing()) {
			xmlCollector = new ToXMLContentHandler();
		}
		stack.push(localName);
		xmlCollector.startElement(uri, localName, qName, attributes);			
	}
    
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (processing()) {
			StringBuffer chars = new StringBuffer().append(ch, start, length);
			String encoded = XSPARQLCharacterEncoder.encode(chars.toString());
			xmlCollector.characters(encoded.toCharArray(), 0, encoded.length());
		}			
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		xmlCollector.endElement(uri, localName, qName);
		stack.pop();
		
		if (!processing()) {
			String xmlString = "";
			String turtleString = "";
			try {
				xmlString = xmlCollector.toString();
				
				if (logger.isDebugEnabled()) {
					logger.debug("Importing XML: "+xmlString);
				}
				
				evaluator.setSource(new StreamSource(new StringReader(xmlString)));
				StringBuilder combined = new StringBuilder();
				
				for (XdmItem item : evaluator) {
					combined.append(item);
				}
				
				Metadata mdata = new Metadata();
				mdata.set(Metadata.CONTENT_TYPE, "text/turtle");
				mdata.set(Metadata.RESOURCE_NAME_KEY, metadata.get(Metadata.RESOURCE_NAME_KEY));
       				
				turtleString = XSPARQLCharacterEncoder.decode(combined.toString());
				
				turtleParser.parse(
						new ByteArrayInputStream(turtleString.getBytes()), 
						new MatchingContentHandler(
						new EmbeddedContentHandler(this.handler), matcher), mdata, context);
				
			} catch (Exception e) {
				throw new SAXException(e.getMessage(), e);
			} finally {
				xmlCollector = null;
			}
		}
	}
	
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
