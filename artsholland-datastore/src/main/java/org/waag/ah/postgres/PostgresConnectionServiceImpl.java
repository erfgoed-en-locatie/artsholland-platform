package org.waag.ah.postgres;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.PlatformConfig;
import org.waag.ah.PlatformConfigHelper;
import org.waag.ah.service.PostgresConnectionService;

@Startup
@Singleton
public class PostgresConnectionServiceImpl implements PostgresConnectionService {
	private static final Logger logger = LoggerFactory
			.getLogger(PostgresConnectionServiceImpl.class);

	private BasicDataSource pgDatasource;
	private PlatformConfig config;
	
	private Connection persistentConnection;

	@PostConstruct
	public void connect() {

		try {
			config = PlatformConfigHelper.getConfig();

			pgDatasource = new BasicDataSource();
			pgDatasource.setDriverClassName("org.postgresql.Driver");

			pgDatasource.setUrl("jdbc:postgresql://localhost:5432/"
					+ config.getString("postgres.database.artsholland"));

			pgDatasource.setUsername(config.getString("postgres.username"));
			pgDatasource.setPassword(config.getString("postgres.password"));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (persistentConnection == null) {
			persistentConnection = pgDatasource.getConnection();
		}
		return persistentConnection;
	}

}
