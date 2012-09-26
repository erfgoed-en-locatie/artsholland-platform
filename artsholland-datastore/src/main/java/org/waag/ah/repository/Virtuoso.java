package org.waag.ah.repository;

import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.RepositoryFactory;

import virtuoso.sesame2.driver.VirtuosoRepository;

import com.useekm.reposail.RepositorySail;

public class Virtuoso implements RepositoryFactory {
	final static Logger logger = LoggerFactory.getLogger(Virtuoso.class);
	
	private RepositorySail repository;
	
	@Override
	public synchronized Sail getSail() throws SailException {
		if (repository == null) {
			repository = new RepositorySail(new VirtuosoRepository("jdbc:virtuoso://localhost:1111", "dba", "dba"));
			repository.initialize();
		}
		return repository;
	}
}
