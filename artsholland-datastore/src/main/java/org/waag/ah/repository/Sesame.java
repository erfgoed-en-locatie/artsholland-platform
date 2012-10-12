package org.waag.ah.repository;

import java.io.File;

import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.nativerdf.NativeStore;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.rdf.sesame.SailFactory;

public class Sesame implements SailFactory {
	private Sail sail;
	
	@Override
	public Sail getSail() throws SailException {
		if (sail == null) {
			try {	
				PlatformConfig config = PlatformConfigHelper.getConfig();
				File dataDir = new File(config.getString("repository.sesame.datastore"));
				sail = new NativeStore(dataDir, "spoc,posc,cosp");				
			} catch (Exception e) {
				throw new SailException(e);
			}
		}
		return sail;
	}
}
