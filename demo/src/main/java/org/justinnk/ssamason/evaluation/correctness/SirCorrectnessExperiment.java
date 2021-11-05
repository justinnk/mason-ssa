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

package org.justinnk.ssamason.evaluation.correctness;

import java.nio.file.Path;
import org.justinnk.ssamason.evaluation.CSVWriter;
import org.justinnk.ssamason.evaluation.DataFrame;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.evaluation.PerformanceMetrics;
import sim.engine.SimState;

/**
 * Base class for experiments that use a sir model to probe the correctness of algorithms . This
 * allows to adapt this (abstract) experiment to VannilaSir and SSASir.
 */
public abstract class SirCorrectnessExperiment extends Experiment {

  /** Number of humans in the model. */
  protected int numHumans;
  /** Graph density (probability for ErdosRenyi-Generator). */
  protected double density;
  /** Number of initially infected humans. */
  protected int initialInfected;

  public SirCorrectnessExperiment(int numHumans, double density, int initialInfected) {
    this.numHumans = numHumans;
    this.density = density;
    this.initialInfected = initialInfected;
  }

  @Override
  protected DataFrame[] execute(SimState model, int replications) {
    double observationStep = 0.25;
    SirModelObserver observer = new SirModelObserver(observationStep);
    DataFrame[] datas = new DataFrame[replications];
    for (int i = 0; i < replications; i++) {
      System.out.println("Running replication " + i);
      model = parameterise();
      // model.setJob(i);
      datas[i] = new DataFrame();
      observer.data = datas[i];
      model.start();
      /* Schedule the observer to observe every observationStep time units. */
      model.schedule.scheduleRepeating(0, observer, observationStep);
      do {
        if (!model.schedule.step(model)) {
          break;
        }
      } while (model.schedule.getTime() < 200);
      model.finish();
    }
    /* replace all sum entries with the mean over all replications. */
    // observer.data.replaceAll((k, v) -> (double) v / (double) replications);
    return datas;
  }

  protected void writeAlgoData(Path path) {
    DataFrame algo = new DataFrame();
    algo.addEntry("rateCalculations", PerformanceMetrics.getStaticMetric("rateCalculations"));
    algo.addEntry(
        "conditionEvaluations", PerformanceMetrics.getStaticMetric("conditionEvaluations"));
    algo.addEntry("randomNumbers", PerformanceMetrics.getStaticMetric("randomNumbers"));
    CSVWriter.writeAndClose(algo, path, true);
    PerformanceMetrics.resetStatics();
  }
}
