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

import java.nio.file.Paths;
import org.justinnk.masonssa.demo.ssa.predatorprey.PredatorPreyModel;
import org.justinnk.masonssa.evaluation.CSVWriter;
import org.justinnk.masonssa.evaluation.DataFrame;
import org.justinnk.masonssa.evaluation.Experiment;
import org.justinnk.masonssa.extension.ssa.StochasticSimulationAlgorithm;
import sim.engine.SimState;

public class PredatorPreyCorrectnessExperiment extends Experiment {

  private final int numWolf;
  private final int numSheep;
  private final String algorithmName;
  private final StochasticSimulationAlgorithm algorithm;

  public PredatorPreyCorrectnessExperiment(
      String algorithmName, StochasticSimulationAlgorithm algorithm, int numWolf, int numSheep) {
    this.numWolf = numWolf;
    this.numSheep = numSheep;
    this.algorithmName = algorithmName;
    this.algorithm = algorithm;
    System.out.println("Evaluating " + algorithmName + " for correctness on predatorprey.");
  }

  @Override
  protected SimState parameterise() {
    PredatorPreyModel model = new PredatorPreyModel(System.nanoTime(), algorithm);
    model.setNumSheep(this.numSheep);
    model.setNumWolves(this.numWolf);
    return model;
  }

  @Override
  protected void evaluate(DataFrame[] results) {
    for (int i = 0; i < results.length; i++) {
      CSVWriter.writeAndClose(
          results[i],
          Paths.get(
              outputPathPrefix
                  + "correctness_data/predatorprey/"
                  + this.algorithmName
                  + "_"
                  + i
                  + ".csv"),
          true);
    }
    System.out.println("Done.");
  }

  @Override
  protected DataFrame[] execute(SimState model, int replications) {
    double observationStep = 0.25;
    PredatorPreyModelObserver observer = new PredatorPreyModelObserver(observationStep);
    DataFrame[] datas = new DataFrame[replications];
    for (int i = 0; i < replications; i++) {
      System.out.println("Running replication " + i);
      // model.setJob(i);
      model = parameterise();
      model.start();
      datas[i] = new DataFrame();
      observer.data = datas[i];
      /* Schedule the observer to observe every observationStep time units. */
      model.schedule.scheduleRepeating(0, observer, observationStep);
      do {
        if (!model.schedule.step(model)) {
          break;
        }
      } while (((PredatorPreyModel) model).getNumSheep() >= 0
          && ((PredatorPreyModel) model).getNumWolves() >= 0
          && model.schedule.getTime() < 15);
      model.finish();
    }
    /* replace all sum entries with the mean over all replications. */
    // observer.data.replaceAll((k, v) -> (double) v / (double) replications);
    return datas;
  }
}
