package org.waag.ah;

import javax.ejb.Local;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;

@Local
public interface RepositoryConnectionFactory {
	SailRepositoryConnection getConnection() throws RepositoryException;
	SailRepositoryConnection getConnection(boolean autoCommit) throws RepositoryException;
}
