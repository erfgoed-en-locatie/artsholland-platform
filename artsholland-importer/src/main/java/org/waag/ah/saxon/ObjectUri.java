package org.waag.ah.saxon;

import java.net.MalformedURLException;
import java.net.URL;

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

import org.apache.commons.configuration.ConfigurationException;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;

@SuppressWarnings("serial")
public class ObjectUri extends ExtensionFunctionDefinition {
	private final PlatformConfig config;

	public ObjectUri() throws ConfigurationException {
		config = PlatformConfigHelper.getConfig();
	}

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}

	@Override
	public int getMaximumNumberOfArguments() {
		return 100;
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",
				"object-uri");
	}

	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.OPTIONAL_STRING;
	}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new ExtensionFunctionCall() {
			public SequenceIterator call(SequenceIterator[] arguments,
					XPathContext context) throws XPathException {
				String uri = config.getString("platform.dataUrl");
				boolean first = true;
				for (SequenceIterator arg : arguments) {
					try {
						// TODO: lowercase?
						
						String s = "";
						
						if (!first) {
							/*
							 * Do not add slash as URI seperator the first time;
							 * baseUri end with /: "http://purl.org/artsholland/1.0/"
							 */
							s = "/";
						}
						
						s += ((StringValue) arg.next()).getStringValue();						
						uri += s.replaceAll("\\/+", "/").replaceAll(" ", "-");
						
					} catch (NullPointerException e) {
						return Value.asIterator(EmptySequence.getInstance());
					}
					first = false;
				}
				try {
					return Value.asIterator(StringValue.makeStringValue(new URL(uri)
							.toExternalForm()));
				} catch (MalformedURLException e) {
					throw new XPathException(e.getMessage(), e);
				}
			}
		};
	}
}
