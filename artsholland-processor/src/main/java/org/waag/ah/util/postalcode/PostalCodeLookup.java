package org.waag.ah.util.postalcode;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.waag.ah.exception.PostalCodeNotFoundException;
import org.waag.ah.service.PostgresConnectionService;

import au.com.bytecode.opencsv.CSVReader;

@Startup
@Singleton
public class PostalCodeLookup {
	private Logger logger = LoggerFactory.getLogger(PostalCodeLookup.class);
	
	static final String SCHEMA_NAME = "postal_code_lookup";
	
	static Connection conn;
		
	@PostConstruct
	public void connect() {
		InitialContext ic;
		try {
			ic = new InitialContext();
			PostgresConnectionService connectionService = (PostgresConnectionService) ic
					.lookup("java:global/artsholland-platform/datastore/PostgresConnectionServiceImpl");
			conn = connectionService.getConnection();
		} catch (Exception e) {
			logger.warn("Could not connect to database 'artsholland' for postal code lookup.", e);
		}
		
		// Import Dutch postal code list
		try {
			importCSV();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	public static void importCSV() throws SQLException, IOException {
		String csvFilename = PostalCodeLookup.class.getResource("nl.csv").getPath();
		
		String sqlDrop = "DROP SCHEMA IF EXISTS " + SCHEMA_NAME + " CASCADE";
		String sqlCreateSchema = "CREATE SCHEMA " + SCHEMA_NAME + ";\n";
		String sqlCreateTable = "CREATE TABLE " + SCHEMA_NAME + ".nl (postal_code_from int, postal_code_to int, city text, municipality text, province text);\n";
		String sqlInsert = "INSERT INTO " + SCHEMA_NAME + ".nl (postal_code_from, postal_code_to, city, municipality, province) VALUES (?, ?, ?, ?, ?);\n";
		//String sqlInsert = "COPY " + SCHEMA_NAME + ".nl FROM '" + csvFilename + "' DELIMITERS ',' CSV HEADER";
		String sqlCreateIndex1 = "CREATE INDEX postal_code_from_idx ON " + SCHEMA_NAME + ".nl (postal_code_from);\n";
		String sqlCreateIndex2 = "CREATE INDEX postal_code_to_idx ON " + SCHEMA_NAME + ".nl (postal_code_to);\n";
		
		Statement stmt = conn.createStatement();
		PreparedStatement prpstmt = conn.prepareStatement(sqlInsert);
		
		stmt.execute(sqlDrop);
		stmt.execute(sqlCreateSchema);
		stmt.execute(sqlCreateTable);
		
		CSVReader reader = new CSVReader(new FileReader(csvFilename));
		List<String[]> csvLines = reader.readAll();
		csvLines.remove(0); // Remove header line
	    for (String[] csvLine : csvLines) {
	    	prpstmt.setInt(1, Integer.parseInt(csvLine[0]));
	    	prpstmt.setInt(2, Integer.parseInt(csvLine[1]));
	    	prpstmt.setString(3, csvLine[2]);
	    	prpstmt.setString(4, csvLine[3]);
	    	prpstmt.setString(5, csvLine[4]);	    	

	    	prpstmt.executeUpdate();	    	
	    }		
		
		stmt.execute(sqlCreateIndex1);
		stmt.execute(sqlCreateIndex2);				
	}

	public static String lookupPostalCode(String postalCode)
			throws PostalCodeNotFoundException {
		if (conn == null) {
			throw new PostalCodeNotFoundException("Postal code lookup failed, no connection to database");
		}
		
		if (postalCode.length() != 6) {
			throw new PostalCodeNotFoundException();
		}
		String numeric = postalCode.substring(0, 4);
		try {
			Statement statement = conn.createStatement();
			String sqlSelect = "SELECT city FROM postal_code_lookup.nl WHERE " + numeric + " BETWEEN postal_code_from AND postal_code_to LIMIT 1";
			
			ResultSet rs = statement.executeQuery(sqlSelect);
			
			while (rs.next()) {
        String city = rs.getString("city");
        return city;
			}
		} catch (SQLException e) {
			throw new PostalCodeNotFoundException("Postal code lookup failed: SQL error",e);
		}
		throw new PostalCodeNotFoundException();
	}

}