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
import org.justinnk.masonssa.demo.ssa.sirs.SirsModel;
import org.justinnk.masonssa.evaluation.CSVWriter;
import org.justinnk.masonssa.evaluation.DataFrame;
import org.justinnk.masonssa.evaluation.Experiment;
import org.justinnk.masonssa.extension.graphs.ErdosRenyiGraphCreator;
import org.justinnk.masonssa.extension.graphs.GraphCreator;
import org.justinnk.masonssa.extension.ssa.StochasticSimulationAlgorithm;
import sim.engine.SimState;

public class SSASirsCorrectnessExperiment extends SirCorrectnessExperiment {

  private StochasticSimulationAlgorithm algorithm;
  private String algorithmName;

  public SSASirsCorrectnessExperiment(
      String algorithmName,
      StochasticSimulationAlgorithm algorithm,
      int numHumans,
      double density,
      int initialInfected) {
    super(numHumans, density, initialInfected);
    this.algorithm = algorithm;
    this.algorithmName = algorithmName;
    System.out.println("Evaluating " + algorithmName + " for correctness on sirs.");
  }

  @Override
  protected SimState parameterise() {
    // GraphCreator graph = new GridGraphCreator(Experiment.seed);
    GraphCreator graph = new ErdosRenyiGraphCreator(Experiment.seed, this.density);
    SirsModel model = new SirsModel(System.nanoTime(), algorithm, graph);
    model.setNumHumans(numHumans);
    model.setInitialInfected(this.initialInfected);
    return model;
  }

  @Override
  protected void evaluate(DataFrame[] results) {
    for (int i = 0; i < results.length; i++) {
      CSVWriter.writeAndClose(
          results[i],
          Paths.get(
              outputPathPrefix + "correctness_data/sirs/" + this.algorithmName + "_" + i + ".csv"),
          true);
    }
    writeAlgoData(
        Paths.get(outputPathPrefix + "performance_data/sirs/" + this.algorithmName + "_algo.csv"));
    System.out.println("Done.");
  }
}
