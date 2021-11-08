/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.evaluation.benchmarks;

import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.graphs.ErdosRenyiGraphCreator;
import org.justinnk.ssamason.extension.graphs.GraphCreator;
import org.openjdk.jmh.annotations.*;
import sim.engine.SimState;

@State(Scope.Thread)
public class BenchmarkSirsErdosRenyi {
  // @Param({ "16", "32", "64", "128", "256", "512", "1024", "2048", "4096" })
  @Param({"512"})
  public int numHumans;

  @Param({"0.24", "0.16", "0.04", "0.01", "0.08", "0.64", "0.02", "0.32", "0.48"})
  public double density;

  public int currentIteration = 0;
  public SimState model;

  @TearDown(Level.Iteration)
  public void TearDown() {
    // System.out.println("finish model");
    model.finish();
  }

  protected GraphCreator getGraph() {
    return new ErdosRenyiGraphCreator(Experiment.seed + currentIteration, this.density);
  }
}
