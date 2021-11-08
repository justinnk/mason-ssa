/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
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
