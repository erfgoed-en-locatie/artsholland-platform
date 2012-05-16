package org.waag.ah;

import java.util.concurrent.Callable;

import org.openrdf.query.MalformedQueryException;

public interface QueryTask extends Callable<Void> {
	long getCount() throws UnsupportedOperationException, MalformedQueryException;
}
