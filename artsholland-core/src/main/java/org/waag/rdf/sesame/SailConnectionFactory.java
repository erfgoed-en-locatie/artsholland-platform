package org.waag.rdf.sesame;

import javax.ejb.Local;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;

@Local
public interface SailConnectionFactory {
	SailRepositoryConnection getConnection() throws RepositoryException;
	SailRepositoryConnection getConnection(boolean autoCommit) throws RepositoryException;
}
