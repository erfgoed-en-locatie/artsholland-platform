//package org.waag.ah.tinkerpop;
//
//import org.openrdf.model.Statement;
//import org.openrdf.model.URI;
//import org.waag.ah.tinkerpop.function.URIFilterPipeFunction;
//
//import com.tinkerpop.pipes.PipeFunction;
//import com.tinkerpop.pipes.filter.FilterFunctionPipe;
//import com.tinkerpop.pipes.util.Pipeline;
//
//public class EnricherCandidatePipeline extends
//		Pipeline<Statement, URI> {
//	private URI object;
//
//	public EnricherCandidatePipeline(URI object) {
//		this.object = object;
//
//		Pipeline<Statement, URI> uriListPipeline = new Pipeline<Statement, URI>();
//		PipeFunction<Statement, Boolean> uriFilter = new URIFilterPipeFunction(object);
//		uriListPipeline.addPipe(new FilterFunctionPipe<Statement>(uriFilter));	
//
////		PipeFunction<Statement, Boolean> uriFilter = new GraphFilterPipeFunction(object);
////		this.addPipe(new FilterFunctionPipe<Statement>(uriFilter));	
//		
//		
//		
//	}
//
//}
