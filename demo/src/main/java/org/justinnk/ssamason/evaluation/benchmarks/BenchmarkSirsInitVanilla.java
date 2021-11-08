/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.evaluation.benchmarks;

import java.util.concurrent.TimeUnit;
import org.justinnk.ssamason.demo.mason.vanillasirs.SirsModel;
import org.justinnk.ssamason.evaluation.Experiment;
import org.openjdk.jmh.annotations.*;

public class BenchmarkSirsInitVanilla extends BenchmarkSirsInit {

  @Setup(Level.Iteration)
  public void SetUp() {
    // System.out.println("setup model");
    model = new SirsModel(Experiment.seed + currentIteration, getGraph());
    ((SirsModel) model).setNumHumans(this.numHumans);
    model.start();
    currentIteration++;
  }

  @BenchmarkMode(Mode.SingleShotTime)
  @OutputTimeUnit(TimeUnit.SECONDS)
  @Benchmark
  public void initVanilla() {
    model.schedule.step(model);
  }
}
