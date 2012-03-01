package org.waag.ah.persistence;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.waag.ah.ObjectConnectionFactory;
import org.waag.ah.model.Production;
import org.waag.ah.model.Room;
import org.waag.ah.model.RoomImpl;

import com.bigdata.rdf.sail.BigdataSailRepository;

@Singleton
public class ObjectConnectionFactoryImpl extends SAILConnectionFactory implements ObjectConnectionFactory {
	
	private ObjectRepositoryConfig config;
	private ObjectRepository repository;
	private ObjectRepositoryFactory repositoryFactory;
	
	@PostConstruct
	public void create() {
		
		repositoryFactory = new ObjectRepositoryFactory();
		config = repositoryFactory.getConfig();
		
		try {		
			
			if (getRepository() == null) {
				super.create();
			}		
						
			config.addConcept(RoomImpl.class);	
			//config.addBehaviour(RoomImpl.class);

			repository = repositoryFactory.createRepository(config, getRepository());
			
		} catch (ObjectStoreConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public ObjectConnection getObjectConnection() throws RepositoryConfigException, RepositoryException {
		
		ObjectConnection conn = repository.getConnection();
		//conn.setNamespace(prefix, name)
		return conn;
		
	}	
	
}