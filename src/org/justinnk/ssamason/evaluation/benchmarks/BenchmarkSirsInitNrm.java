package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.ssamason.sirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.NextReactionMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkSirsInitNrm extends BenchmarkSirsInit {
	
	@Setup(Level.Iteration)
	public void SetUp() {
		model = new SirsModel(Experiment.seed + currentIteration, new NextReactionMethod(), getGraph());
		((SirsModel) model).setNumHumans(this.numHumans);
		model.start();
		currentIteration++;
	}

	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Benchmark
	public void initNrm() {
		model.schedule.step(model);
	}
}
