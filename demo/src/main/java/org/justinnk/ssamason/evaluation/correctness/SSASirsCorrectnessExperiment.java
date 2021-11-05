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

import java.nio.file.Paths;
import org.justinnk.ssamason.demo.ssamason.sirs.SirsModel;
import org.justinnk.ssamason.evaluation.CSVWriter;
import org.justinnk.ssamason.evaluation.DataFrame;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.graphs.ErdosRenyiGraphCreator;
import org.justinnk.ssamason.extension.graphs.GraphCreator;
import org.justinnk.ssamason.extension.ssa.StochasticSimulationAlgorithm;
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
