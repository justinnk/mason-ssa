/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.evaluation.benchmarks;

import java.util.concurrent.TimeUnit;
import org.justinnk.masonssa.demo.masonssa.sirs.SirsModel;
import org.justinnk.masonssa.evaluation.Experiment;
import org.justinnk.masonssa.extension.ssa.NextReactionMethod;
import org.openjdk.jmh.annotations.*;

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
