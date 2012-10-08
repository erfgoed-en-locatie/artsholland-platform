package org.waag.ah.repository;

import java.io.File;

import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.nativerdf.NativeStore;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.RepositoryFactory;

public class Sesame implements RepositoryFactory {
	private Sail repository;
	
	@Override
	public Sail getSail() throws SailException {
		if (repository == null) {
			try {	
				PlatformConfig config = PlatformConfigHelper.getConfig();
				File dataDir = new File(config.getString("sesame.datastore"));
				repository = new NativeStore(dataDir, "spoc,posc,cosp");				
			} catch (Exception e) {
				throw new SailException(e);
			}
		}
		return repository;
	}
}
