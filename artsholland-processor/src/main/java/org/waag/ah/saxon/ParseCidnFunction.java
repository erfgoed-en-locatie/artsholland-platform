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
public class ParseCidnFunction extends ExtensionFunctionDefinition {

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",
				"parse-cidn");
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
				String cidn = "";
				try {
					cidn = ((StringValue) arguments[0].next()).getStringValue();

					// Check whether cidn is a valid cidn number, 
					// return empty sequence otherwise
					// Cidn is valid when longer than, well, let's 
					// say 20 characters!
					
					// TODO: length limit of 20 characters is arbitrary! FIX!
					
					boolean valid = cidn.length() >= 20;
					
					if (!valid) {
						return Value.asIterator(EmptySequence.getInstance());
					}					

				} catch (Exception e) {
					return Value.asIterator(EmptySequence.getInstance());
				}
				return Value.asIterator(StringValue.makeStringValue(cidn));
			}
		};
	}
}
