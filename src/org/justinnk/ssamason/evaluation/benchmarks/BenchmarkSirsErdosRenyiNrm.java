package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.demo.ssamason.sirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.NextReactionMethod;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchmarkSirsErdosRenyiNrm extends BenchmarkSirsErdosRenyi {

	public static boolean wasStopped = false;

	@Setup(Level.Iteration)
	public void SetUp() {
		// System.out.println("setup model");
		model = new SirsModel(Experiment.seed + currentIteration, new NextReactionMethod(), getGraph());
		((SirsModel) model).setNumHumans(this.numHumans);
		model.start();
		currentIteration++;
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
