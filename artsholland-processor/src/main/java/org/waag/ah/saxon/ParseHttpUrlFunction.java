package org.waag.ah.saxon;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ParseHttpUrlFunction extends ExtensionFunctionDefinition {
	private static final Logger logger = LoggerFactory.getLogger(ParseHttpUrlFunction.class);
	
	private UrlValidator urlValidator = UrlValidator.getInstance();
	private EmailValidator emailValidator = EmailValidator.getInstance();
	
	/*
	 * @author Raoul Wissink <raoul@waag.org>
	 * @see net.sf.saxon.lib.ExtensionFunctionDefinition#getArgumentTypes()
	 */
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {SequenceType.OPTIONAL_STRING};
	}

	/*
	 * @author Raoul Wissink <raoul@waag.org>
	 * @see net.sf.saxon.lib.ExtensionFunctionDefinition#getFunctionQName()
	 */
	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension", "parse-http-url");
	}

	/*
	 * @author Raoul Wissink <raoul@waag.org>
	 * @see net.sf.saxon.lib.ExtensionFunctionDefinition#getResultType(net.sf.saxon.value.SequenceType[])
	 */
	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.OPTIONAL_STRING;
	}

	/*
	 * Validate URL.
	 * 
	 * @author Raoul Wissink <raoul@waag.org>
	 * @see net.sf.saxon.lib.ExtensionFunctionDefinition#makeCallExpression()
	 */
	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new ExtensionFunctionCall() {
			public SequenceIterator call(SequenceIterator[] arguments,
					XPathContext context) throws XPathException {
				
				String text = "";
				try {
					text = ((StringValue) arguments[0].next()).getStringValue();
				
					text = text.trim();

					if (text.length() == 0) {
						return Value.asIterator(EmptySequence.getInstance());
					}
					// Filter out e-mail addresses.
					if (emailValidator.isValid(text)) {
						return Value.asIterator(StringValue.makeStringValue("mailto:" + text));
					}
					// Try to fix URL.
					if (!text.startsWith("http://") && !text.startsWith("https://")) {
						text = "http://" + text;
					}
					text = text.replace(" ", "%20");
					text = text.replace("\\", "%5C");

					// Validate URL.
					// URLs with unicode characters like 'ë' are not accepted
					// by Apache Commons UrlValidator, but are valid URLs					
					String asciiText = text.replaceAll("[^\\x00-\\x7F]", "");
					if (!urlValidator.isValid(asciiText)) {						
						
						logger.warn("Invalid URL: " + text);
						return Value.asIterator(EmptySequence.getInstance());
					}			

				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}
				return Value.asIterator(StringValue.makeStringValue(text));
			}
		};
	}
}
