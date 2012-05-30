package org.waag.ah.useekm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.rest.model.AHRDFNamespaces;

import com.useekm.indexing.postgis.PostgisIndexMatcher;
import com.useekm.indexing.postgis.PostgisIndexerSettings;

public class PostgisIndexerSettingsGenerator {
	final static Logger logger = LoggerFactory.getLogger(PostgisIndexerSettingsGenerator.class);
	
	private static final Map<String, Boolean> PREDICATES = createMap();
	
	private static Map<String, Boolean> createMap() {
		Map<String, Boolean> result = new LinkedHashMap<String, Boolean>();
		
		result.put("ah:geometry", false);
		result.put("dc:description", true);
		result.put("dc:title", true);
		
		return Collections.unmodifiableMap(result);	
	}

	public static PostgisIndexerSettings generateSettings() throws ConfigurationException {
		
		PropertiesConfiguration platformConfig = PlatformConfigHelper.getConfig(); 
		
		// Initialize the datasource to be used for connections to Postgres:
		BasicDataSource pgDatasource = new BasicDataSource();
		pgDatasource.setDriverClassName("org.postgresql.Driver");
		
		// TODO: complete jdbc url in artsholland.properties?
		pgDatasource.setUrl("jdbc:postgresql://localhost:5432/" + platformConfig.getString("useekm.database"));
		
		pgDatasource.setUsername(platformConfig.getString("postgres.username"));
		pgDatasource.setPassword(platformConfig.getString("postgres.password"));

		// Initialize the settings for the Postgis Indexer:
		PostgisIndexerSettings settings = new PostgisIndexerSettings();

		settings.setDataSource(pgDatasource);
		
		// add matchers for each predicate for which statements need to be indexed:
		settings.setMatchers(getMatchers(PREDICATES));

		// Initialize the IndexingSail that wraps your BigdataSail:
		settings.initialize(true);

		return settings;
	}
	
	private static List<PostgisIndexMatcher> getMatchers(Map<String, Boolean> predicates) {

		ArrayList<PostgisIndexMatcher> matchers = new ArrayList<PostgisIndexMatcher>();
		
		for (Map.Entry<String, Boolean> entry : predicates.entrySet()) {
			
			String predicate = AHRDFNamespaces.getFullURI(entry.getKey());
			
			if (predicate != null) {
			
				PostgisIndexMatcher matcher = new PostgisIndexMatcher();
				
				matcher.setPredicate(predicate);
				if (entry.getValue()) {
					matcher.setSearchConfig("simple");	
				}
				matchers.add(matcher);	
				
			}
			
		}
		
		return matchers;
		
	}
}
