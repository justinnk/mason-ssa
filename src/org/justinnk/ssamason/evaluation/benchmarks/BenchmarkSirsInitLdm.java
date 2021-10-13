package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.ssamason.sirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.LogarithmicDirectMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkSirsInitLdm extends BenchmarkSirsInit {
	
	@Setup(Level.Iteration)
	public void SetUp() {
		// System.out.println("setup model");
		model = new SirsModel(Experiment.seed + currentIteration, new LogarithmicDirectMethod(), getGraph());
		((SirsModel) model).setNumHumans(this.numHumans);
		model.start();
		currentIteration++;
	}

	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Benchmark
	public void initLdm() {
		model.schedule.step(model);
		model.schedule.step(model);
	}
}
