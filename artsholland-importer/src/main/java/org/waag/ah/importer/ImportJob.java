package org.waag.ah.importer;


public interface ImportJob {
	public static String STRATEGY_FULL = "full";
	public static String STRATEGY_INCREMENTAL = "incremental";
//	public void doImport() throws Exception;
	
	public String getJobId();
	public long getTimestamp();
}
