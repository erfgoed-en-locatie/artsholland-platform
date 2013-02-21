package org.waag.ah.repository;

import java.io.File;

import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.rdf.sesame.SailFactory;

public class SesameMemory implements SailFactory {
	private MemoryStore sail;
	
	@Override
	public Sail getSail() throws SailException {
		if (sail == null) {
			try {	
				PlatformConfig config = PlatformConfigHelper.getConfig();
				
				File dataDir = new File(config.getString("repository.SesameMemory.datastore"));
				sail = new MemoryStore(dataDir);
				sail.setSyncDelay(config.getLong("repository.SesameMemory.syncDelay", 100L));

//				Repository myRepository = new SailRepository(memStore);
//				myRepository.initialize();
				
//				sail = new NativeStore(dataDir, "spoc,posc,cosp");				
			} catch (Exception e) {
				throw new SailException(e);
			}
		}
		return sail;
	}
}
