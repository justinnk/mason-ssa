package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.graphs.ErdosRenyiGraphCreator;
import org.justinnk.ssamason.extension.graphs.GraphCreator;
import org.openjdk.jmh.annotations.*;
import sim.engine.SimState;

@State(Scope.Thread)
public class BenchmarkSirsInit {
	@Param({"1024"})
	public int numHumans;

	@Param({ "0.48", "0.02", "0.08", "0.16", "0.24", "0.64", "0.32", "0.04" })
	public double density;

	public int currentIteration = 0;
	public SimState model;

	@TearDown(Level.Iteration)
	public void TearDown() {
		model.finish();
	}
	
	protected GraphCreator getGraph() {
		return new ErdosRenyiGraphCreator(Experiment.seed + currentIteration, this.density);
	}
}
