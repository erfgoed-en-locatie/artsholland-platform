package org.waag.ah;

import java.io.IOException;

import javax.ejb.Local;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

@Local
public interface RepositoryConnectionFactory {
	
	public Repository getRepository();
	
	public RepositoryConnection getConnection() 
			throws IOException, RepositoryException;
	
	public RepositoryConnection getReadOnlyConnection()
		throws IOException, RepositoryException;

}
