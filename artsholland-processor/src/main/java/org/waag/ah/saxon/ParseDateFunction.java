package org.waag.ah.saxon;

import java.text.SimpleDateFormat;

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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@SuppressWarnings("serial")
public class ParseDateFunction extends ExtensionFunctionDefinition {

	@Override
	public int getMinimumNumberOfArguments() {
		return 1;
	}

	@Override
	public int getMaximumNumberOfArguments() {
		return 3;
	}

	/**
	 * Arguments: 1: Date/time value 2: Input format pattern (leave empty for
	 * autodetect) 3: Output format pattern (leave empty for full ISO 8601)
	 * 
	 * @see http
	 *      ://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat
	 *      .html
	 */
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING,
				SequenceType.OPTIONAL_STRING, SequenceType.OPTIONAL_STRING };
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("waag", "http://waag.org/saxon-extension",
				"parse-date");
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
				try {
					DateTime dt = null;
					String value = ((StringValue) arguments[0].next()).getStringValue();
					String fmtInput = ((StringValue) arguments[1].next())
							.getStringValue();
					
					if (value.length() == 0) {
						return Value.asIterator(EmptySequence.getInstance());
					}
					
					// String fmtOutput =
					// ((StringValue)arguments[2].next()).getStringValue();
					if (!fmtInput.isEmpty()) {
						DateTimeFormatter fmt = DateTimeFormat.forPattern(fmtInput);						
						dt = fmt.parseDateTime(value);
					} else {
						dt = new DateTime(value);
					}
					// return Value.asIterator(DateTimeValue.fromJavaDate(dt.toDate()));	

					String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(dt.toDate());					
					return Value.asIterator(StringValue.makeStringValue(formattedDate));
				} catch (Exception e) {
					throw new XPathException(e.getMessage(), e);
				}
			}
		};
	}
}
