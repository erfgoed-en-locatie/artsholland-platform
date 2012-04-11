package org.waag.ah;

import java.util.concurrent.Callable;

public interface QueryTask extends Callable<Void> {
	long getCount() throws UnsupportedOperationException;
}
