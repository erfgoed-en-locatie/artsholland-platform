package org.waag.ah;

import java.io.IOException;

import javax.ejb.Local;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.Sail;

import com.bigdata.journal.Journal;

@Local
public interface RepositoryConnectionFactory {
	
	// TODO: Get rid of this method.
	public Journal getJournal();
	
	public Sail getSail();
	
	public RepositoryConnection getConnection() 
			throws IOException, RepositoryException;
}
