/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.evaluation.benchmarks;

import java.util.concurrent.TimeUnit;
import org.justinnk.masonssa.demo.masonssa.sirs.SirsModel;
import org.justinnk.masonssa.evaluation.Experiment;
import org.justinnk.masonssa.extension.graphs.GridGraphCreator;
import org.justinnk.masonssa.extension.ssa.LogarithmicDirectMethod;
import org.openjdk.jmh.annotations.*;

/** Benchmark for Logarithmic Direct Method on Erdos-Renyi Graph. */
@State(Scope.Thread)
public class BenchmarkSirsGridLdm extends BenchmarkSirsGrid {

  @Setup(Level.Iteration)
  public void SetUp() {
    // System.out.println("setup model");
    model =
        new SirsModel(
            Experiment.seed + currentIteration,
            new LogarithmicDirectMethod(),
            new GridGraphCreator());
    ((SirsModel) model).setNumHumans(this.gridSize * this.gridSize);
    model.start();
    model.schedule.step(model);
    currentIteration++;
  }

  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Benchmark
  public void stepLdm() {
    model.schedule.step(model);
  }
}
