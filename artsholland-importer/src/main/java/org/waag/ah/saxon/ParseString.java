package org.waag.ah.saxon;

import org.waag.ah.tika.util.XSPARQLCharacterEncoder;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

@SuppressWarnings("serial")
public class ParseString extends ExtensionFunctionDefinition {

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {SequenceType.OPTIONAL_STRING};
	}

//	@Override
//	public int getMinimumNumberOfArguments() {
//		return 0;
//	}
	
//	@Override
//	public int getMaximumNumberOfArguments() {
//		return 1;
//	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension", "parse-string");
	}

	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.OPTIONAL_STRING;
	}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            public SequenceIterator call(SequenceIterator[] arguments, XPathContext context) throws XPathException {
            	String text = "";
            	try {
	            	text = ((StringValue)arguments[0].next()).getStringValue();
	            	text = XSPARQLCharacterEncoder.encode(text);
            	} catch (NullPointerException e) {}
            	return Value.asIterator(StringValue.makeStringValue(text));
            }
        };
	}
}
