/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.evaluation.benchmarks;

import java.util.concurrent.TimeUnit;
import org.justinnk.ssamason.demo.ssamason.sirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.LogarithmicDirectMethod;
import org.openjdk.jmh.annotations.*;

public class BenchmarkSirsErdosRenyiLdm extends BenchmarkSirsErdosRenyi {
  @Setup(Level.Iteration)
  public void SetUp() {
    // System.out.println("setup model");
    model =
        new SirsModel(
            Experiment.seed + currentIteration, new LogarithmicDirectMethod(), getGraph());
    ((SirsModel) model).setNumHumans(this.numHumans);
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
