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

import java.util.concurrent.TimeUnit;
import org.justinnk.ssamason.demo.ssamason.predatorprey.PredatorPreyModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.FirstReactionMethod;
import org.openjdk.jmh.annotations.*;

public class BenchmarkSheepFrm extends BenchmarkSheep {

  @Setup(Level.Iteration)
  public void SetUp() {
    // System.out.println("setup model");
    model = new PredatorPreyModel(Experiment.seed + currentIteration, new FirstReactionMethod());
    ((PredatorPreyModel) model).setNumSheep(numSheep);
    ((PredatorPreyModel) model).setNumWolves(0);
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
