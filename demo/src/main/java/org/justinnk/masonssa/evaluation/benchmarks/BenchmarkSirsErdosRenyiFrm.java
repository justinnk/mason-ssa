/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.evaluation.benchmarks;

import java.util.concurrent.TimeUnit;
import org.justinnk.masonssa.demo.masonssa.sirs.SirsModel;
import org.justinnk.masonssa.evaluation.Experiment;
import org.justinnk.masonssa.extension.ssa.FirstReactionMethod;
import org.openjdk.jmh.annotations.*;

public class BenchmarkSirsErdosRenyiFrm extends BenchmarkSirsErdosRenyi {
  @Setup(Level.Iteration)
  public void SetUp() {
    // System.out.println("setup model");
    model =
        new SirsModel(Experiment.seed + currentIteration, new FirstReactionMethod(), getGraph());
    ((SirsModel) model).setNumHumans(this.numHumans);
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
