package org.waag.ah.persistence;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.openrdf.repository.Repository;
import org.waag.ah.RepositoryConnectionFactory;

public abstract class AbstractConnectionFactory {
	
	protected abstract Repository createRepository(Properties props);
	
    protected Properties loadProperties(String resource) throws IOException {
        Properties p = new Properties();
        InputStream is = getClass().getResourceAsStream(resource);
        p.load(new InputStreamReader(new BufferedInputStream(is)));
        return p;
    }
}
