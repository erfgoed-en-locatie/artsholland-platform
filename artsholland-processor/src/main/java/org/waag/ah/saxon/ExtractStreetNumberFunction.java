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

@SuppressWarnings("serial")
public class ExtractStreetNumberFunction extends ExtensionFunctionDefinition {

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",
				"extract-street-number");
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
				String text = "";
				try {
					text = ((StringValue) arguments[0].next()).getStringValue();

					// Split street name and number + addition, return number + addition
					// Find first number, and return everything including and after that
					// position.
					// TODO: Problem some street names have numbers in them...
					
					int i;
					for (i = 0; i < text.length(); i++) {
						char c = text.charAt(i);
						if (Character.isDigit(c)) {
							break;
						}
					}
					
					if (i == text.length()) {
						return Value.asIterator(EmptySequence.getInstance());
					}
					
					text = text.substring(i, text.length()).trim();

				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}
				return Value.asIterator(StringValue.makeStringValue(text));
			}
		};
	}
}
