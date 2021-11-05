/*
 * MIT License
 *
 * Copyright (c) 2021 Justin Kreikemeyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
