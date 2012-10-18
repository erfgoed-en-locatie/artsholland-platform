package org.waag.ah.tika;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
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
import net.sf.saxon.s9api.XdmNode;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ContentHandlerDecorator;
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
import org.waag.ah.saxon.AddressUriFunction;
import org.waag.ah.saxon.ClassUriFunction;
import org.waag.ah.saxon.ObjectUriFunction;
import org.waag.ah.saxon.ParseBooleanFunction;
import org.waag.ah.saxon.ParseCidnFunction;
import org.waag.ah.saxon.ParseDateTimeFunction;
import org.waag.ah.saxon.ParseDecimalFunction;
import org.waag.ah.saxon.ParseHttpUrlFunction;
import org.waag.ah.saxon.ParseLocaleFunction;
import org.waag.ah.saxon.ParseNonZeroNumber;
import org.waag.ah.saxon.ParseStringFunction;
import org.waag.ah.saxon.PostalCodeFunction;
import org.waag.ah.saxon.StreetNumberFunction;
import org.waag.ah.saxon.UpperCaseFirstFunction;
import org.waag.ah.saxon.UrlConcatFunction;
import org.waag.ah.saxon.WKTGeometryFunction;
import org.waag.ah.saxon.WebContentTypeFunction;
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
		
		try {
			XSPARQLProcessor xp = new XSPARQLProcessor();			
			String q = xp.process(xquery);
			
			// TODO: This is nasty, but we need the namespaces from our XSPARQL query.
			Matcher matcher = Pattern.compile("PREFIX ([a-zA-Z]+): <(.*)>").matcher(q);
			while (matcher.find()) {
				super.startPrefixMapping(matcher.group(1), matcher.group(2));
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
			config.registerExtensionFunction(new ClassUriFunction());
			config.registerExtensionFunction(new AddressUriFunction());
			config.registerExtensionFunction(new ParseDecimalFunction());
			config.registerExtensionFunction(new ParseLocaleFunction());
			config.registerExtensionFunction(new ParseStringFunction());			
			config.registerExtensionFunction(new ParseBooleanFunction());
			config.registerExtensionFunction(new ParseHttpUrlFunction());
			config.registerExtensionFunction(new ParseNonZeroNumber());
			config.registerExtensionFunction(new UpperCaseFirstFunction());
			config.registerExtensionFunction(new PostalCodeFunction());
			config.registerExtensionFunction(new StreetNumberFunction());
			config.registerExtensionFunction(new WKTGeometryFunction());
			config.registerExtensionFunction(new WebContentTypeFunction());
			config.registerExtensionFunction(new ParseCidnFunction());
			config.registerExtensionFunction(new UrlConcatFunction());
			
			Processor processor = new Processor(config);
			XQueryCompiler compiler = processor.newXQueryCompiler();	
			XQueryExecutable compiled = compiler.compile(q);
			evaluator = compiled.load();
			
			if (includes != null) {
				DocumentBuilder docBuilder = processor.newDocumentBuilder();
				for (Entry<String, URI> item : includes.entrySet()) {
//					logger.info(item.getValue().toString());
					XdmNode document = docBuilder.build(new File(item.getValue().getPath()));
					evaluator.setExternalVariable(new QName(item.getKey()), document);				
				}
			}
		} catch (Exception e) {
			throw new ParserException(e);
		}		
	}

	private boolean processing() {
		return stack.size() > 0;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
//		logger.debug("Processing element: "+localName);
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
				
				if (logger.isDebugEnabled()) {
					logger.debug("Importing XML: "+xmlString);
				}
				
				evaluator.setSource(new StreamSource(new StringReader(xmlString)));
				turtleString = evaluator.evaluate().toString();
				
				if (turtleString.split("\n\n").length < 2) {
					return;
				}
				
				Metadata mdata = new Metadata();
				mdata.set(Metadata.CONTENT_TYPE, "text/turtle");
				mdata.set(Metadata.RESOURCE_NAME_KEY, metadata.get(Metadata.RESOURCE_NAME_KEY));
				
				turtleParser.parse(
						new ByteArrayInputStream(turtleString.getBytes()), 
						new MatchingContentHandler(
						this.handler, matcher), mdata, context);

			} catch (NoSuchElementException e) {
				logger.info("Not enough data to proceed: "+xmlString);
			} catch (Exception e) {
//				logger.info(xmlString);
//				logger.info(turtleString);
				throw new SAXException(e);
			} finally {
				xmlCollector = null;
			}
		}
	}
}
