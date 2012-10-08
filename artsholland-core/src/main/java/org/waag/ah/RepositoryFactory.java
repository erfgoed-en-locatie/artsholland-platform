package org.waag.ah;

import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;

public interface RepositoryFactory {
	public Sail getSail() throws SailException;
}
