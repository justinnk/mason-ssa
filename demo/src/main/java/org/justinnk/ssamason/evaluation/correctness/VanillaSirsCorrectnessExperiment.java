/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
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
