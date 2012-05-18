package org.waag.ah.cascading.tap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import cascading.jdbc.TupleRecord;

public class ImportJobInputFormat<T extends URL> implements
		InputFormat<String, T>, JobConfigurable {
	private ImportJobConfiguration importConf;

	@Override
	public void configure(JobConf job) {
		this.importConf = new ImportJobConfiguration(job);
	}
	
	@Override
	public RecordReader<String, T> getRecordReader(InputSplit split, JobConf conf,
			Reporter reporter) throws IOException {
		return new ImportResourceReader((JobInputSplit) split, conf);
	}
	  
	@Override
	public InputSplit[] getSplits(JobConf conf, int chunks) throws IOException {
		List<URL> resources = importConf.getImportResources();
		
		System.out.println("CHUNKS: "+chunks);

		long count = resources.size();
		long chunkSize = (count / chunks);

		InputSplit[] splits = new InputSplit[chunks];

		// Split the rows into n-number of chunks and adjust the last chunk
		// accordingly
		for (int i = 0; i < chunks; i++) {
			JobInputSplit split;
			if (i + 1 == chunks) {
				split = new JobInputSplit(i * chunkSize, count);
			} else {
				split = new JobInputSplit(i * chunkSize, i * chunkSize
						+ chunkSize);
			}
			splits[i] = split;
		}

		return splits;
	}

	protected static class JobInputSplit implements InputSplit {
		private long end = 0;
		private long start = 0;

		/** Default Constructor */
		public JobInputSplit() {
		}

		/**
		 * Convenience Constructor
		 * 
		 * @param start
		 *            the index of the first row to select
		 * @param end
		 *            the index of the last row to select
		 */
		public JobInputSplit(long start, long end) {
			this.start = start;
			this.end = end;
		}

		/** {@inheritDoc} */
		public String[] getLocations() throws IOException {
			// TODO Add a layer to enable SQL "sharding" and support locality
			return new String[] {};
		}

		/** @return The index of the first row to select */
		public long getStart() {
			return start;
		}

		/** @return The index of the last row to select */
		public long getEnd() {
			return end;
		}

		/** @return The total row count in this split */
		public long getLength() throws IOException {
			return end - start;
		}

		/** {@inheritDoc} */
		public void readFields(DataInput input) throws IOException {
			start = input.readLong();
			end = input.readLong();
		}

		/** {@inheritDoc} */
		public void write(DataOutput output) throws IOException {
			output.writeLong(start);
			output.writeLong(end);
		}
	}
	  
	protected class ImportResourceReader implements RecordReader<String, T> {
	    private JobInputSplit split;
	    private long pos = 0;
		private JobConf conf;
		private List<URL> resources;
	    
		public ImportResourceReader(JobInputSplit split, JobConf conf) {
			this.split = split;
			this.conf = conf;
			this.resources = importConf.getImportResources();
		}

		@Override
		public boolean next(String key, T value) throws IOException {
			if (resources.size() == 0) {
				return false;
			}

			// Set the key field value as the output key value
			// key.set( pos + split.getStart() );
			String url = resources.remove(0).toExternalForm();
			// value.readFields( results );
			// pos++;

			return true;
		}

		@Override
		public String createKey() {
			return "";
		}

		@Override
		public T createValue() {
//			return ReflectionUtils.newInstance( inputClass, job );
			return null;
		}

		@Override
		public long getPos() throws IOException {
			return pos;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public float getProgress() throws IOException {
			return pos / split.getLength();
		}
	}

	public static void setInput(JobConf job, Class<TupleRecord> inputClass,
			int concurrentReads) {
	    job.setInputFormat(ImportJobInputFormat.class);

	    ImportJobConfiguration importJobConf = new ImportJobConfiguration(job);

	    importJobConf.setInputClass(inputClass);
//	    dbConf.setInputFieldNames( fieldNames );
//	    dbConf.setMaxConcurrentReadsNum( concurrentReads );
	}
}
