package org.justinnk.ssamason.evaluation.correctness;

import org.justinnk.ssamason.evaluation.DataFrame;
import sim.engine.SimState;
import sim.engine.Steppable;

import java.lang.reflect.InvocationTargetException;

public class PredatorPreyModelObserver implements Steppable {
	private static final long serialVersionUID = 1L;
	public DataFrame data = new DataFrame();
//	private double stepSize = 1.0;

	public PredatorPreyModelObserver(double stepSize) {
//		this.stepSize = stepSize;
	}

	@Override
	public void step(SimState state) {
//		int currTime = (int) (state.schedule.getTime() * (1.0 / this.stepSize));
		try {
			int sheep = (int) state.getClass().getMethod("getNumSheep").invoke(state);
			int wolf = (int) state.getClass().getMethod("getNumWolves").invoke(state);
			//data.compute("sheep", currTime, (k, v) -> v == null ? sheep : (double) v + sheep);
			//data.compute("wolf", currTime, (k, v) -> v == null ? wolf : (double) v + wolf);
			data.addEntry("sheep", sheep);
			data.addEntry("wolf", wolf);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
}
