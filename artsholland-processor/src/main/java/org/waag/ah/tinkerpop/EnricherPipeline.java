package org.waag.ah.tinkerpop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.waag.rdf.sesame.EnricherConfig;
import org.waag.rdf.sesame.NamedGraph;

import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.branch.CopySplitPipe;
import com.tinkerpop.pipes.branch.FairMergePipe;
import com.tinkerpop.pipes.sideeffect.GroupByPipe;
import com.tinkerpop.pipes.transform.ScatterPipe;
import com.tinkerpop.pipes.transform.SideEffectCapPipe;
import com.tinkerpop.pipes.transform.TransformFunctionPipe;
import com.tinkerpop.pipes.util.Pipeline;

public class EnricherPipeline extends Pipeline<Statement, Statement> {
	
	@SuppressWarnings("rawtypes")
	public EnricherPipeline(EnricherConfig... configs) {
		super();
		
		// Create Graph objects from individual statements.
		GroupByPipe<Statement, URI, Statement> groupByPipe = new GroupByPipe<Statement, URI, Statement>(
			new StatementKeyFunction(), 
			new StatementValueFunction());
		Pipeline<Statement, Graph> groupingPipeline = new Pipeline<Statement, Graph>();
		groupingPipeline.addPipe(groupByPipe);
		groupingPipeline.addPipe(new SideEffectCapPipe<Statement, Map<URI, List<Statement>>>(groupByPipe));
		groupingPipeline.addPipe(new ScatterPipe<Map<URI, List<Statement>>, Entry<URI, List<Statement>>>());
		groupingPipeline.addPipe(new TransformFunctionPipe<Entry<URI, List<Statement>>, NamedGraph>(new BuildNamedGraphPipeFunction()));
		this.addPipe(groupingPipeline);
		
		List<Pipe> enrichers = new ArrayList<Pipe>();
		for (EnricherConfig config : configs) {
			enrichers.add(EnricherFactory.getInstance(config));
		}
		
		// Send incoming data through all enricher instances.
		CopySplitPipe<NamedGraph> enricherPipe = new CopySplitPipe<NamedGraph>(enrichers);
		FairMergePipe<List<Statement>> mergerPipe = new FairMergePipe<List<Statement>>(enricherPipe.getPipes());

		this.addPipe(enricherPipe);
		this.addPipe(mergerPipe);
		this.addPipe(new ScatterPipe<List<Statement>, Statement>());
	}
}
