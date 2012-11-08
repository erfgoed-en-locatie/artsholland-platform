package org.waag.ah.util.postalcode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.configuration.ConfigurationException;
import org.waag.ah.exception.PostalCodeNotFoundException;
import org.waag.ah.importer.tam.TAMParser;
import org.waag.ah.service.PostgresConnectionService;

@Startup
@Singleton
public class PostalCodeLookup {
	
	static final String SCHEMA_NAME = "postal_code_lookup";
	
	static Connection connection;
		
	@PostConstruct
	public void connect() throws NamingException, SQLException {
		InitialContext ic = new InitialContext();
		PostgresConnectionService connectionService = (PostgresConnectionService) ic
				.lookup("java:global/artsholland-platform/datastore/PostgresConnectionServiceImpl");
		connection = connectionService.getConnection();
		
		// Import Dutch postal code list
		//importCSV();
	}
	
	public static void importCSV() throws SQLException {
		String csvFilename = PostalCodeLookup.class.getResource("nl.csv").getPath();
		
		String sqlDrop = "DROP SCHEMA IF EXISTS " + SCHEMA_NAME + " CASCADE";
		String sqlCreateSchema = "CREATE SCHEMA " + SCHEMA_NAME + ";\n";
		String sqlCreateTable = "CREATE TABLE " + SCHEMA_NAME + ".nl (postal_code_from int, postal_code_to int, city text, municipality text, province text);\n";
		String sqlCreateIndex1 = "CREATE INDEX postal_code_from_idx ON " + SCHEMA_NAME + ".nl (postal_code_from);\n";
		String sqlCreateIndex2 = "CREATE INDEX postal_code_to_idx ON " + SCHEMA_NAME + ".nl (postal_code_to);\n";
		
		String sqlInsert = "COPY " + SCHEMA_NAME + ".nl FROM '" + csvFilename + "' DELIMITERS ',' CSV HEADER"; 
		
		Statement statement = connection.createStatement();
		statement.execute(sqlDrop);
		statement.execute(sqlCreateSchema);
		statement.execute(sqlCreateTable);
		statement.execute(sqlCreateIndex1);
		statement.execute(sqlCreateIndex2);
		statement.executeUpdate(sqlInsert);		
	}

	public static String lookupPostalCode(String postalCode)
			throws PostalCodeNotFoundException {
		
		if (postalCode.length() != 6) {
			throw new PostalCodeNotFoundException();
		}	
		String numeric = postalCode.substring(0, 4);
		try {
			Statement statement = connection.createStatement();
			String sqlSelect = "SELECT city FROM postal_code_lookup.nl WHERE " + numeric + " BETWEEN postal_code_from AND postal_code_to LIMIT 1";
			
			ResultSet rs = statement.executeQuery(sqlSelect);
			
			while (rs.next()) {
        String city = rs.getString("city");
        return city;
			}
		} catch (SQLException e) {
			throw new PostalCodeNotFoundException();
		}
		throw new PostalCodeNotFoundException();
	}

}