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
import org.justinnk.ssamason.demo.mason.vanillasirs.SirsModel;
import org.justinnk.ssamason.evaluation.CSVWriter;
import org.justinnk.ssamason.evaluation.DataFrame;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.graphs.ErdosRenyiGraphCreator;
import org.justinnk.ssamason.extension.graphs.GraphCreator;
import sim.engine.SimState;

public class VanillaSirsCorrectnessExperiment extends SirCorrectnessExperiment {

  public VanillaSirsCorrectnessExperiment(int numHumans, double density, int initialInfected) {
    super(numHumans, density, initialInfected);
    System.out.println("Evaluating vanilla model for correctness on sirs.");
  }

  @Override
  protected SimState parameterise() {
    // GraphCreator graph = new GridGraphCreator(Experiment.seed);
    GraphCreator graph = new ErdosRenyiGraphCreator(Experiment.seed, this.density);
    SirsModel model = new SirsModel(System.nanoTime(), graph);
    model.setNumHumans(this.numHumans);
    model.setInitialInfected(this.initialInfected);
    return model;
  }

  @Override
  protected void evaluate(DataFrame[] results) {
    for (int i = 0; i < results.length; i++) {
      CSVWriter.writeAndClose(
          results[i],
          Paths.get(outputPathPrefix + "correctness_data/sirs/vanilla_" + i + ".csv"),
          true);
    }
    writeAlgoData(Paths.get(outputPathPrefix + "performance_data/sirs/Vanilla_algo.csv"));
    System.out.println("Done.");
  }
}
