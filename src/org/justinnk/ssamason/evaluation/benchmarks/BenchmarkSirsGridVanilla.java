package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.mason.vanillasirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.graphs.GridGraphCreator;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/** Benchmark for vanilla nrm on Erdos-Renyi Graph. */
@State(Scope.Thread)
public class BenchmarkSirsGridVanilla extends BenchmarkSirsGrid {

	public static boolean wasStopped = false;

	@Setup(Level.Iteration)
	public void SetUp() {
		// System.out.println("setup model");
		currentIteration++;
		model = new SirsModel(Experiment.seed + currentIteration, new GridGraphCreator());
		((SirsModel) model).setNumHumans(this.gridSize * this.gridSize);
		model.start();
		model.schedule.step(model);
	}

	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Benchmark
	public void stepVanilla() {
		do {
			model.schedule.step(model);
		} while (wasStopped);
	}
}
