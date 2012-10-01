package org.waag.ah.tinkerpop;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.waag.ah.rdf.EnricherConfig;
import org.waag.ah.tinkerpop.AbstractProcessor;

public class XsdDateTimeProcessor extends AbstractProcessor {

	public XsdDateTimeProcessor(EnricherConfig config) {
		super(config);
	}

	@Override
	public Statement process(EnricherConfig config, Statement statement) {
		if (Literal.class.isAssignableFrom(statement.getObject().getClass())) {
			Literal value = (Literal) statement.getObject();
			if (value.getDatatype() != null && isDateTimeLiteral(value)) {
				ValueFactory vf = config.getValueFactory();
				return vf.createStatement(statement.getSubject(),
						statement.getPredicate(),
						vf.createLiteral(value.calendarValue()));
			}
		}
		return statement;
	}
	
	private boolean isDateTimeLiteral(Literal value) {
		URI dataType = value.getDatatype();
		return dataType.getNamespace().equals("http://www.w3.org/2001/XMLSchema#")
				&& dataType.getLocalName().equals("dateTime");
	}
}
