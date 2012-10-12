package org.waag.rdf.sesame;

import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;

public interface SailFactory {
	public Sail getSail() throws SailException;
}
