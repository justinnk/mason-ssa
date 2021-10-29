package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.ssamason.sirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.OptimizedDirectMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkSirsErdosRenyiOdm extends BenchmarkSirsErdosRenyi {
	@Setup(Level.Iteration)
	public void SetUp() {
		// System.out.println("setup model");
		model = new SirsModel(Experiment.seed + currentIteration, new OptimizedDirectMethod(), getGraph());
		((SirsModel) model).setNumHumans(this.numHumans);
		model.start();
		model.schedule.step(model);
		currentIteration++;
	}

	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Benchmark
	public void stepOdm() {
		model.schedule.step(model);
	}
}