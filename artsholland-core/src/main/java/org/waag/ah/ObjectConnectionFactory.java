package org.waag.ah;

import javax.ejb.Local;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;

@Local
public interface ObjectConnectionFactory {
	
	public ObjectConnection getObjectConnection()
			throws RepositoryException, RepositoryConfigException;
	
}
