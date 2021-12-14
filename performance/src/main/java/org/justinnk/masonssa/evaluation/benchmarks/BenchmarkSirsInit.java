/*
 * Copyright 2021 Justin Kreikemeyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.justinnk.masonssa.evaluation.benchmarks;

import org.justinnk.masonssa.evaluation.Experiment;
import org.justinnk.masonssa.extension.graphs.ErdosRenyiGraphCreator;
import org.justinnk.masonssa.extension.graphs.GraphCreator;
import org.openjdk.jmh.annotations.*;
import sim.engine.SimState;

@State(Scope.Thread)
public class BenchmarkSirsInit {
  @Param({"1024"})
  public int numHumans;

  @Param({"0.48", "0.02", "0.08", "0.16", "0.24", "0.64", "0.32", "0.04"})
  public double density;

  public int currentIteration = 0;
  public SimState model;

  @TearDown(Level.Iteration)
  public void TearDown() {
    model.finish();
  }

  protected GraphCreator getGraph() {
    return new ErdosRenyiGraphCreator(Experiment.seed + currentIteration, this.density);
  }
}
