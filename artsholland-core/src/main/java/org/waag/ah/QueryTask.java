package org.waag.ah;

import java.util.concurrent.Callable;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

public interface QueryTask extends Callable<Void> {
	long getCount() throws UnsupportedOperationException,
			MalformedQueryException, RepositoryException,
			QueryEvaluationException;
}
