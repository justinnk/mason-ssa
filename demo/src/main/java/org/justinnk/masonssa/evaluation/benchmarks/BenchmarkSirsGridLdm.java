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

import java.util.concurrent.TimeUnit;
import org.justinnk.masonssa.demo.ssa.sirs.SirsModel;
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
