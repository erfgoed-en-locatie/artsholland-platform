package org.waag.ah;

import javax.ejb.Local;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.sail.Sail;
import org.waag.ah.exception.ConnectionException;

@Local
public interface RepositoryConnectionFactory {
	Sail getSail() throws ConnectionException;
	Sail getIndexingSail() throws ConnectionException;
	RepositoryConnection getConnection() throws ConnectionException;
}
