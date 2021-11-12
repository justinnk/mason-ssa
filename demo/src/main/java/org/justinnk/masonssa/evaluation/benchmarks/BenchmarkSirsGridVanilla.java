/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.evaluation.benchmarks;

import java.util.concurrent.TimeUnit;
import org.justinnk.masonssa.demo.mason.vanillasirs.SirsModel;
import org.justinnk.masonssa.evaluation.Experiment;
import org.justinnk.masonssa.extension.graphs.GridGraphCreator;
import org.openjdk.jmh.annotations.*;

/** Benchmark for vanilla nrm on Erdos-Renyi Graph. */
@State(Scope.Thread)
public class BenchmarkSirsGridVanilla extends BenchmarkSirsGrid {

  public static boolean wasStopped = false;

  @Setup(Level.Iteration)
  public void SetUp() {
    // System.out.println("setup model");
    currentIteration++;
    model = new SirsModel(Experiment.seed + currentIteration, new GridGraphCreator());
    ((SirsModel) model).setNumHumans(this.gridSize * this.gridSize);
    model.start();
    model.schedule.step(model);
  }

  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Benchmark
  public void stepVanilla() {
    do {
      model.schedule.step(model);
    } while (wasStopped);
  }
}
