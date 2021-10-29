package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.ssamason.predatorprey.PredatorPreyModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.FirstReactionMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkSheepFrm extends BenchmarkSheep {
	
	@Setup(Level.Iteration)
	public void SetUp() {
		// System.out.println("setup model");
		model = new PredatorPreyModel(Experiment.seed + currentIteration, new FirstReactionMethod());
		((PredatorPreyModel) model).setNumSheep(numSheep);
		((PredatorPreyModel) model).setNumWolves(0);
		model.start();
		model.schedule.step(model);
		currentIteration++;
	}

	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Benchmark
	public void stepFrm() {
		model.schedule.step(model);
	}
}