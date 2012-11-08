package org.waag.ah.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.waag.ah.Service;

public interface PostgresConnectionService extends Service {
	Connection getConnection() throws SQLException;
}
