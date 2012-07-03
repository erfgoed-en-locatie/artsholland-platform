package org.waag.ah.tinkerpop.pipe;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;

import com.tinkerpop.pipes.AbstractPipe;

public class StatementGeneratorPipe extends
		AbstractPipe<InputStream, List<Statement>> {
	final static Logger logger = LoggerFactory.getLogger(StatementGeneratorPipe.class);
	private PlatformConfig config;

	public StatementGeneratorPipe() {
		try {
			config = PlatformConfigHelper.getConfig();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected List<Statement> processNextStart() throws NoSuchElementException {
		try {
			List<Statement> statements = new ArrayList<Statement>();
			RDFXMLParser parser = new RDFXMLParser();
			parser.setRDFHandler(new RDFStatementHandler(statements));
			parser.parse(this.starts.next(), config.getString("platform.classUri"));
//			logger.info(statements.toString());		
			return statements;
		} catch (Exception e) {
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
