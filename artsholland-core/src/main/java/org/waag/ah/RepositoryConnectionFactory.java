package org.waag.ah;

import java.io.IOException;

import javax.ejb.Local;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.Sail;

@Local
public interface RepositoryConnectionFactory {
	
	public Sail getSail();
	
	public RepositoryConnection getConnection() 
			throws IOException, RepositoryException;
}
