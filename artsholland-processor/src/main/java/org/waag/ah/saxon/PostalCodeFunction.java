package org.waag.ah.saxon;

import java.util.Arrays;
import java.util.List;

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
public class PostalCodeFunction extends ExtensionFunctionDefinition {

	public static final List<String> INCORRECT_POSTAL_CODES =
			Arrays.asList("1234XX", "9999XX", "XXXX", "0000");
	
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",
				"postal-code");
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
				String postalCode = "";
				try {
					postalCode = ((StringValue) arguments[0].next()).getStringValue();
					postalCode = postalCode.replaceAll("\\s","").toUpperCase();
					
					if (postalCode.length() != 6) {
						return Value.asIterator(EmptySequence.getInstance());
					}
					
					for (String incorrectPostalCode : INCORRECT_POSTAL_CODES) {
						if (postalCode.startsWith(incorrectPostalCode)) {
							return Value.asIterator(EmptySequence.getInstance());
						}
					}
					
				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}				
				return Value.asIterator(StringValue.makeStringValue(postalCode));
			}
		};
	}
}
