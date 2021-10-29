package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.ssamason.sirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.FirstReactionMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkSirsInitFrm extends BenchmarkSirsInit {
	
	@Setup(Level.Iteration)
	public void SetUp() {
		// System.out.println("setup model");
		model = new SirsModel(Experiment.seed + currentIteration, new FirstReactionMethod(), getGraph());
		((SirsModel) model).setNumHumans(this.numHumans);
		model.start();
		currentIteration++;
	}

	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Benchmark
	public void initFrm() {
		model.schedule.step(model);
		model.schedule.step(model);
	}
}