package org.justinnk.ssamason.evaluation.correctness;

import org.justinnk.ssamason.demo.ssamason.predatorprey.PredatorPreyModel;
import org.justinnk.ssamason.evaluation.CSVWriter;
import org.justinnk.ssamason.evaluation.DataFrame;
import org.justinnk.ssamason.evaluation.Experiment;
import org.justinnk.ssamason.extension.ssa.StochasticSimulationAlgorithm;
import sim.engine.SimState;

import java.nio.file.Paths;

public class PredatorPreyCorrectnessExperiment extends Experiment {

	private final int numWolf;
	private final int numSheep;
	private final String algorithmName;
	private final StochasticSimulationAlgorithm algorithm;

	public PredatorPreyCorrectnessExperiment(String algorithmName, StochasticSimulationAlgorithm algorithm, int numWolf,
			int numSheep) {
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
			CSVWriter.writeAndClose(results[i],
				Paths.get(outputPathPrefix + "correctness_data/predatorprey/" + this.algorithmName + "_" + i + ".csv"), true);
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
			//model.setJob(i);
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
			} while (((PredatorPreyModel) model).getNumSheep() >= 0 && ((PredatorPreyModel) model).getNumWolves() >= 0
					&& model.schedule.getTime() < 15);
			model.finish();
		}
		/* replace all sum entries with the mean over all replications. */
		//observer.data.replaceAll((k, v) -> (double) v / (double) replications);
		return datas;
	}
}