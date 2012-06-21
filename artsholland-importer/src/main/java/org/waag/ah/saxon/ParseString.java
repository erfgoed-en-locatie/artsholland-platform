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

import org.waag.ah.tika.util.XSPARQLCharacterEncoder;

@SuppressWarnings("serial")
public class ParseString extends ExtensionFunctionDefinition {
	
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {SequenceType.OPTIONAL_STRING};
	}

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
			public SequenceIterator call(SequenceIterator[] arguments,
					XPathContext context) throws XPathException {
				String text = "";
				try {
					text = ((StringValue) arguments[0].next()).getStringValue();
					text = text.replaceAll("\"", "&quot;");
					text = XSPARQLCharacterEncoder.encode(text);

					// Temporary fix, see:
					// https://sourceforge.net/mailarchive/message.php?msg_id=29432380
					if (text.matches("^([a-zA-Z]*):(.+)$")) {
						text = text.replaceFirst(":", " :");
					}
				} catch (NullPointerException e) {					
				}
				
				if (text.length() == 0) {
					return Value.asIterator(EmptySequence.getInstance());
				} else {
					return Value.asIterator(StringValue.makeStringValue(text));
				}
			}
		};
	}
}
