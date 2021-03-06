package org.waag.ah.tinkerpop;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
		AbstractPipe<String, List<Statement>> {
	final static Logger logger = LoggerFactory.getLogger(StatementGeneratorPipe.class);
	private PlatformConfig config;

	public StatementGeneratorPipe() throws ConfigurationException {
		config = PlatformConfigHelper.getConfig();
	}

	@Override
	protected List<Statement> processNextStart() throws NoSuchElementException {
		List<Statement> statements = new ArrayList<Statement>();
		String data = this.starts.next();
		InputStream is = new ByteArrayInputStream(data.getBytes());//"UTF-8"
		try {
			RDFXMLParser parser = new RDFXMLParser();
			parser.setRDFHandler(new RDFStatementHandler(statements));
			parser.parse(is, config.getString("platform.classUri"));
		} catch (Exception e) {
			logger.info(data);
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return statements;
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
