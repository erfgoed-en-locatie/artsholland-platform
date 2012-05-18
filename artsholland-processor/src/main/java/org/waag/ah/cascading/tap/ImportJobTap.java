package org.waag.ah.cascading.tap;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;

import cascading.scheme.Scheme;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.TapException;
import cascading.tap.hadoop.TapCollector;
import cascading.tap.hadoop.TapIterator;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;

@SuppressWarnings("serial")
public class ImportJobTap extends Tap {
	private List<URL> resources;
	int concurrentReads = -1;

	public ImportJobTap(List<URL> resources, Scheme scheme) {
		super(scheme, SinkMode.APPEND);
		this.resources = resources;
	}

	@Override
	public void sourceInit(JobConf conf) throws IOException {
		FileInputFormat.setInputPaths(conf, getPath());
		ImportJobConfiguration.configure(conf, resources);
		super.sourceInit(conf);
	}

	@Override
	public boolean deletePath(JobConf conf) throws IOException {
		return false;
	}

	@Override
	public Path getPath() {
		return new Path("/tmp/cascading/quartz");
	}

	@Override
	public long getPathModified(JobConf arg0) throws IOException {
		return System.currentTimeMillis();
	}

	@Override
	public boolean makeDirs(JobConf arg0) throws IOException {
		return false;
	}

	@Override
	public TupleEntryIterator openForRead(JobConf conf) throws IOException {
		return new TupleEntryIterator(getSourceFields(), new TapIterator(this, conf));
	}

	@Override
	public TupleEntryCollector openForWrite(JobConf conf) throws IOException {
		if (!isSink())
			throw new TapException(
					"this tap may not be used as a sink, no TableDesc defined");

		return new TapCollector(this, conf);
	}

	@Override
	public boolean pathExists(JobConf arg0) throws IOException {
		return false;
	}

}
