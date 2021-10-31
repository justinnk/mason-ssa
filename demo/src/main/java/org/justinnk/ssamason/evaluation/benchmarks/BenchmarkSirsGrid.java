package org.justinnk.ssamason.evaluation.benchmarks;

import org.openjdk.jmh.annotations.*;
import sim.engine.SimState;

@State(Scope.Thread)
public abstract class BenchmarkSirsGrid {
	@Param({ "8", "256", "16", "1000", "24", "300", "768", "32", "48", "4", "64", "512", "96", "128", "160", "384" })
	public int gridSize;

	public int currentIteration = 0;
	public SimState model;

	@TearDown(Level.Iteration)
	public void TearDown() {
		// System.out.println("finish model");
		model.finish();
	}
}
