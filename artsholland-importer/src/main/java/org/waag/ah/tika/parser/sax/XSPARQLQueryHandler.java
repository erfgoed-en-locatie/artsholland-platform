package org.waag.ah.tika.parser.sax;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.deri.xquery.saxon.createScopedDatasetExtFunction;
import org.deri.xquery.saxon.deleteScopedDatasetExtFunction;
import org.deri.xquery.saxon.jsonDocExtFunction;
import org.deri.xquery.saxon.scopedDatasetPopResultsExtFunction;
import org.deri.xquery.saxon.sparqlQueryExtFunction;
import org.deri.xquery.saxon.sparqlScopedDatasetExtFunction;
import org.deri.xquery.saxon.turtleGraphToURIExtFunction;
import org.deri.xsparql.rewriter.XSPARQLProcessor;
import org.openrdf.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.exception.ParserException;
import org.waag.ah.saxon.ObjectUriFunction;
import org.waag.ah.saxon.ParseDateTimeFunction;
import org.waag.ah.saxon.ParseLocaleFunction;
import org.waag.ah.saxon.ParseStringFunction;
import org.waag.ah.tika.parser.rdf.TurtleParser;
import org.waag.ah.tika.util.XSPARQLCharacterEncoder;
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
	private ContentHandler handler;
	private org.apache.tika.sax.xpath.Matcher matcher;
	private Stack<String> stack;
	private boolean parsingTurtle;
	private HashMap<String, String> namespaces;
	
	public XSPARQLQueryHandler(ContentHandler handler, Metadata metadata,
			ParseContext context, InputStream xquery) throws ParserException {
		this(handler, metadata, context, xquery, null);
	}
	
	/**
	 * @param handler
	 * @param metadata
	 * @param context
	 * @param xquery
	 * @param includes
	 * @throws ParserException
	 * @deprecated
	 */
	public XSPARQLQueryHandler(ContentHandler handler, Metadata metadata,
			ParseContext context, InputStream xquery, Map<String, URI> includes)
					throws ParserException {
		this(handler, metadata, context, new InputStreamReader(xquery), includes);
	}
	
	public XSPARQLQueryHandler(ContentHandler handler, Metadata metadata,
			ParseContext context, Reader xquery, Map<String, URI> includes)
			throws ParserException {
		this.matcher = new XPathParser("rdf", RDF.NAMESPACE).parse("/rdf:RDF/descendant::node()");
		this.handler = handler;
		super.setContentHandler(this.handler);
		this.context = context;
		this.metadata = metadata; 
		this.turtleParser = new TurtleParser();
		this.stack = new Stack<String>();
		this.namespaces = new HashMap<String, String>();
		
		try {
			XSPARQLProcessor xp = new XSPARQLProcessor();			
			String q = xp.process(xquery);
			
			// TODO: This is nasty, but we need the namespaces from our XSPARQL query.
			namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
			Matcher matcher = Pattern.compile("PREFIX ([a-zA-Z]+): <(.*)>").matcher(q);
			while (matcher.find()) {
				namespaces.put(matcher.group(1), matcher.group(2));
			}

			Configuration config = new Configuration();
			
			// XSPARQL specific functions (see: https://sourceforge.net/mailarchive/message.php?msg_id=29435521).
		    config.registerExtensionFunction(new sparqlQueryExtFunction());
		    config.registerExtensionFunction(new turtleGraphToURIExtFunction());
		    config.registerExtensionFunction(new createScopedDatasetExtFunction());
		    config.registerExtensionFunction(new sparqlScopedDatasetExtFunction());
		    config.registerExtensionFunction(new deleteScopedDatasetExtFunction());
		    config.registerExtensionFunction(new scopedDatasetPopResultsExtFunction());
		    config.registerExtensionFunction(new jsonDocExtFunction());
		    
			// Custom XSPARQL functions.
			config.registerExtensionFunction(new ParseDateTimeFunction());
			config.registerExtensionFunction(new ObjectUriFunction());
			config.registerExtensionFunction(new ParseLocaleFunction());
			config.registerExtensionFunction(new ParseStringFunction());			
			
			Processor processor = new Processor(config);
			XQueryCompiler compiler = processor.newXQueryCompiler();	
			XQueryExecutable compiled = compiler.compile(q);
			evaluator = compiled.load();
			
			if (includes != null) {
				DocumentBuilder docBuilder = processor.newDocumentBuilder();
				for (Entry<String, URI> item : includes.entrySet()) {
					XdmNode document = docBuilder.build(new File(item.getValue()));
					evaluator.setExternalVariable(new QName(item.getKey()), document);				
				}
			}
		} catch (Exception e) {
			throw new ParserException(e.getMessage());
		}			
	}

	private boolean processing() {
		return stack.size() > 0;
	}

	@Override
	public void startDocument() throws SAXException {
		for (Entry<String, String> mapping : namespaces.entrySet()) {
			super.startPrefixMapping(mapping.getKey(), mapping.getValue());
		}
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (parsingTurtle == true) {
			logger.info("PARSING TURTLE: parsingTurtle = "+localName);
		}
		if (!processing()) {
			xmlCollector = new ToXMLContentHandler();
		}
		// Make sure element attribute values don't contain unwanted HTML chars.
		for (int idx=0; idx<attributes.getLength(); idx++) {
			try {
				((AttributesImpl) attributes).setValue(idx,
						StringEscapeUtils.escapeHtml(attributes.getValue(idx)));
			} catch (RuntimeException e) {
				continue;
			}
		}
		stack.push(localName);
		xmlCollector.startElement(uri, localName, qName, attributes);			
	}
    
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (processing()) {
			String data = new StringBuffer().append(ch, start, length).toString();
			data = XSPARQLCharacterEncoder.encode(data);
			xmlCollector.characters(data.toCharArray(), 0, data.length());
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
				
				// TODO: Hope this is fixed in XSPARQL 0.4
//				xmlString = xmlString.replace("https://", "http://");
				
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
				turtleString = XSPARQLCharacterEncoder.repairInvalidTypeURIs(turtleString);
//				logger.info(turtleString);
				
				turtleParser.parse(
						new ByteArrayInputStream(turtleString.getBytes()), 
						new MatchingContentHandler(
						new EmbeddedContentHandler(this.handler), matcher), mdata, context);
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				logger.info("XML: \n"+xmlString);
				logger.info("TURTLE: \n"+turtleString);
				throw new SAXException(e.getMessage(), e);
			} finally {
				xmlCollector = null;
			}
		}
	}
}
