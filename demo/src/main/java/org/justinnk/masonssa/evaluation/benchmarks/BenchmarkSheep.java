/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.evaluation.benchmarks;

import org.openjdk.jmh.annotations.*;
import sim.engine.SimState;

@State(Scope.Thread)
public class BenchmarkSheep {

  @Param("10")
  int numSheep;

  public int currentIteration = 0;
  public SimState model;

  @TearDown(Level.Iteration)
  public void TearDown() {
    // System.out.println("finish model");
    model.finish();
  }
}
