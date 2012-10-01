package org.waag.ah.sesame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.binary.BinaryRDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchedStatementWriter {
	final static Logger logger = LoggerFactory.getLogger(BatchedStatementWriter.class);
	
	private RepositoryConnection connection;
	private FileOutputStream out;
	private RDFWriter writer;
	private URI context;

	private List<File> files = new ArrayList<File>(); 
	
	private long total = 0;
	private long count = 0;
	private long limit;
	
	public BatchedStatementWriter(RepositoryConnection connection, URI context) {
		this(connection, context, 100000);
	}
	
	public BatchedStatementWriter(RepositoryConnection connection, URI context, long limit) {
		this.connection = connection;
		this.context = context;
		this.limit = limit;
	}
	
	public void write(Statement statement) throws RDFHandlerException, IOException {
		if (writer == null || count >= limit) {
			count = 0;
			openWriter();
		}
		writer.handleStatement(statement);
		total++;
		count++;
	}
	
	public synchronized void commit() throws RDFParseException, RepositoryException, IOException {
		closeWriter();
		long done = 0;
		try {
			for (File file : files) {
				done++;
				logger.info("Adding batch file to commit ("+done+"/"+files.size()+")");
				connection.add(file, context.stringValue(), RDFFormat.BINARY, context);
			}
			connection.commit();
			logger.info("Statements comitted, added "+total+" items");
		} finally {
			cleanup();
		}
	}
	
	public synchronized void rollback() {
		try {
			logger.info("Rolling back transaction");
			connection.rollback();
		} catch (RepositoryException e) {
			logger.warn("Error rolling back transaction");
		} finally {
			cleanup();
		}
	}
	
	private void openWriter() throws IOException, RDFHandlerException {
		if (writer != null) {
			closeWriter();
		}
		File tempFile = File.createTempFile(this.getClass().getName(), ".tmp");
		out = new FileOutputStream(tempFile);
		writer = new BinaryRDFWriter(out);
		writer.startRDF();
		files.add(tempFile);
		logger.info("Added new batch file: count="+files.size()+", filename="+tempFile.getAbsolutePath());
	}
	
	private void closeWriter() {
		try {
			writer.endRDF();
			out.close();	
		} catch (Exception e) {
			logger.error("Error closing writer: "+e.getMessage());
		}
	}
	
	private synchronized void cleanup() {
		for (File file : files) {
			file.delete();
		}	
		files.clear();
	}
}
