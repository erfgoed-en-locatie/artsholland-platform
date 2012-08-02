package org.waag.ah.saxon;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.FloatValue;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

@SuppressWarnings("serial")
public class ParseNonZeroNumber extends ExtensionFunctionDefinition {
	
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {SequenceType.OPTIONAL_FLOAT};
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension", "parse-non-zero-number");
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
				float number = 0;
				try {
					//Item vis = arguments[0].next();
					//String koek = vis.getStringValue();
					//number = Double.parseDouble(koek);
					number = ((FloatValue) arguments[0].next()).getFloatValue();					
				} catch (NullPointerException e) {					
				}
				
				if (number == 0) {
					return Value.asIterator(EmptySequence.getInstance());
				} else {	
					return Value.asIterator(StringValue.makeStringValue(Float.toString(number)));
				}
			}
		};
	}
}
