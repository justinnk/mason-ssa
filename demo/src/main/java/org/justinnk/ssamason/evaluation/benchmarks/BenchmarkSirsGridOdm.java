/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.evaluation.benchmarks;

import java.util.concurrent.TimeUnit;
import org.justinnk.ssamason.demo.ssamason.sirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.graphs.GridGraphCreator;
import org.justinnk.ssamason.extension.ssa.OptimizedDirectMethod;
import org.openjdk.jmh.annotations.*;

/** Benchmark for Optimized Direct Method on Erdos-Renyi Graph. */
@State(Scope.Thread)
public class BenchmarkSirsGridOdm extends BenchmarkSirsGrid {

  @Setup(Level.Iteration)
  public void SetUp() {
    // System.out.println("setup model");
    currentIteration++;
    model =
        new SirsModel(
            Experiment.seed + currentIteration,
            new OptimizedDirectMethod(),
            new GridGraphCreator());
    ((SirsModel) model).setNumHumans(this.gridSize * this.gridSize);
    model.start();
    model.schedule.step(model);
  }

  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Benchmark
  public void stepOdm() {
    model.schedule.step(model);
  }
}
