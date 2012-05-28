package org.waag.ah.tinkerpop.pipes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;

import com.tinkerpop.pipes.AbstractPipe;


public class StatementGeneratorPipe extends
		AbstractPipe<InputStream, List<Statement>> {

	@Override
	protected List<Statement> processNextStart() throws NoSuchElementException {
		try {
			List<Statement> statements = new ArrayList<Statement>();
			RDFXMLParser parser = new RDFXMLParser();
			parser.setRDFHandler(new RDFStatementHandler(statements));
			parser.parse(this.starts.next(), "http://data.artsholland.com");
			return statements;
		} catch (Exception e) {
//			e.printStackTrace();
			throw new NoSuchElementException(e.getMessage());
		}
	}

	private class RDFStatementHandler extends RDFHandlerBase {
		private List<Statement> statements;

		public RDFStatementHandler(List<Statement> statements) {
			this.statements = statements;
		}

		@Override
		public void handleStatement(Statement st) throws RDFHandlerException {
			statements.add(st);
		}
	}
}
