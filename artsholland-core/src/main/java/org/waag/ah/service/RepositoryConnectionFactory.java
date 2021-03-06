package org.waag.ah.service;

import javax.ejb.Local;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

@Local
public interface RepositoryConnectionFactory {
	RepositoryConnection getConnection() throws RepositoryException;
	RepositoryConnection getConnection(boolean autoCommit) throws RepositoryException;
	RepositoryConnection getReadWriteConnection() throws RepositoryException;
}
