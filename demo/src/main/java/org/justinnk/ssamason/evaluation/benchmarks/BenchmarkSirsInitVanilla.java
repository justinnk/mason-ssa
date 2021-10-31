package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.mason.vanillasirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkSirsInitVanilla extends BenchmarkSirsInit {
	
	@Setup(Level.Iteration)
	public void SetUp() {
		// System.out.println("setup model");
		model = new SirsModel(Experiment.seed + currentIteration, getGraph());
		((SirsModel) model).setNumHumans(this.numHumans);
		model.start();
		currentIteration++;
	}

	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Benchmark
	public void initVanilla() {
		model.schedule.step(model);
	}
}
