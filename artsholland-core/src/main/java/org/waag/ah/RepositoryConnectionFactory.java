package org.waag.ah;

import java.io.IOException;

import javax.ejb.Local;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.Sail;

import com.bigdata.journal.Journal;

@Local
public interface RepositoryConnectionFactory {
	
//	public Repository getRepository();
	public Journal getJournal();
	public Sail getSail();
	
	public RepositoryConnection getConnection() 
			throws IOException, RepositoryException;
	
	public RepositoryConnection getReadOnlyConnection()
		throws IOException, RepositoryException;
}
