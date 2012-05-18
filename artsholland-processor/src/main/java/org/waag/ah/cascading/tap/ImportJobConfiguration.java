package org.waag.ah.cascading.tap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.mapred.JobConf;

import cascading.jdbc.db.DBInputFormat;
import cascading.jdbc.db.DBWritable;

public class ImportJobConfiguration {
	private JobConf job;
	private List<URL> resourceList;
	
	public static final String RESOURCE_LIST_PROPERTY = "mapred.importjob.resources";
	public static final String INPUT_CLASS_PROPERTY = "mapred.importjob.input.class";
	
	public ImportJobConfiguration(JobConf job) {
		this.job = job;
	}

	public static void configure(JobConf job, List<URL> resources) {
		job.set(RESOURCE_LIST_PROPERTY, StringUtils.join(resources, ","));
	}
	
	Class<?> getInputClass() {
		return job.getClass(ImportJobConfiguration.INPUT_CLASS_PROPERTY,
				DBInputFormat.NullDBWritable.class);
	}

	void setInputClass(Class<? extends DBWritable> inputClass) {
		job.setClass(ImportJobConfiguration.INPUT_CLASS_PROPERTY, inputClass,
				DBWritable.class);
	}

	public List<URL> getImportResources() {
		if (resourceList == null) {
			try {
				resourceList = parseResourceList();
			} catch (MalformedURLException e) {
				resourceList = new ArrayList<URL>();
			}
		}
		return resourceList;
	}
	
	private List<URL> parseResourceList() throws MalformedURLException {
		String[] urls = job.get(RESOURCE_LIST_PROPERTY).split(",");
		List<URL> resources = new ArrayList<URL>(); 
		for(String url : urls) {
			resources.add(new URL(url));
		}
		return resources;		
	}
}
