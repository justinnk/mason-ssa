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

package org.justinnk.masonssa.evaluation.correctness;

import java.nio.file.Path;
import org.justinnk.masonssa.evaluation.CSVWriter;
import org.justinnk.masonssa.evaluation.DataFrame;
import org.justinnk.masonssa.evaluation.Experiment;
import org.justinnk.masonssa.evaluation.PerformanceMetrics;
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
