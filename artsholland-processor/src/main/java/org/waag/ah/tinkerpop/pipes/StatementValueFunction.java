package org.waag.ah.tinkerpop.pipes;

import org.openrdf.model.Statement;

import com.tinkerpop.pipes.PipeFunction;

public class StatementValueFunction implements PipeFunction<Statement, Statement> {

	@Override
	public Statement compute(Statement st) {
		return st;
	}
}
