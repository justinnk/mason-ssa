package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.ssamason.predatorprey.PredatorPreyModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.NextReactionMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkSheepNrm extends BenchmarkSheep {
	
	public static boolean wasStopped = false;
	
	@Setup(Level.Iteration)
	public void SetUp() {
		model = new PredatorPreyModel(Experiment.seed + currentIteration, new NextReactionMethod());
		((PredatorPreyModel) model).setNumSheep(numSheep);
		((PredatorPreyModel) model).setNumWolves(0);
		model.start();
		model.schedule.step(model);
		currentIteration++;
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