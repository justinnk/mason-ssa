package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.ssamason.sirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.graphs.GridGraphCreator;
import org.justinnk.ssamason.extension.ssa.NextReactionMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/** Benchmark for Next Reaction Method on Erdos-Renyi Graph. */
@State(Scope.Thread)
public class BenchmarkSirsGridNrm extends BenchmarkSirsGrid {

	public static boolean wasStopped = false;

	@Setup(Level.Iteration)
	public void SetUp() {
		// System.out.println("setup model");
		currentIteration++;
		model = new SirsModel(Experiment.seed + currentIteration, new NextReactionMethod(), new GridGraphCreator());
		((SirsModel) model).setNumHumans(this.gridSize * this.gridSize);
		model.start();
		model.schedule.step(model);
	}

	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Benchmark
	public void stepNrm() {
		do {
			model.schedule.step(model);
		} while (wasStopped);
	}
}
