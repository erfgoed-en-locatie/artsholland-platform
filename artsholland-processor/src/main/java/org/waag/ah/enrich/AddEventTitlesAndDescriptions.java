//package org.waag.ah.enrich;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//public class AddEventTitlesAndDescriptions implements Job {
//
//	public final String selectQuery = "" +
//			"SELECT DISTINCT ?uri " +
//			"WHERE { " + 
//			"    ?uri a <ah:Event> ; " +
//			"    dc:title ?title ; " +
//			"    dc:description ?description . " +
//			"    FILTER (?title = \"\" || ?description = \"\") " +
//			"}";
//
//	@Override
//	public void execute(JobExecutionContext context)
//			throws JobExecutionException {
//	}
//	
////	public TupleQuery getSelectQuery(RepositoryConnection conn)
////			throws RepositoryException, MalformedQueryException {
////		return conn.prepareTupleQuery(QueryLanguage.SPARQL, selectQuery);
////	}
////	public void enrichObject(URI uri, Map<URI, Value> data) {
////	}
//}
