package org.waag.ah.cascading.tap;

import java.io.IOException;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;

import cascading.jdbc.TupleRecord;
import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

@SuppressWarnings("serial")
public class ImportJobScheme extends Scheme {

	public ImportJobScheme(Fields fields, String[] urlsSinkColumnNames) {
	}

	@Override
	public void sourceInit(Tap tap, JobConf conf) throws IOException {
		int concurrentReads = ((ImportJobTap) tap).concurrentReads;
		ImportJobInputFormat.setInput(conf, TupleRecord.class, concurrentReads);
	}

	@Override
	public void sinkInit(Tap tap, JobConf conf) throws IOException {
	}

	@Override
	public Tuple source(Object key, Object value) {
		return ((TupleRecord) value).getTuple();
	}

	@Override
	public void sink(TupleEntry tupleEntry, OutputCollector outputCollector)
			throws IOException {
	}
}
