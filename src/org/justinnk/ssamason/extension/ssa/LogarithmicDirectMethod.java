package org.justinnk.ssamason.extension.ssa;

import org.justinnk.ssamason.extension.Action;
import org.justinnk.ssamason.extension.SSASimState;
import sim.engine.SimState;
import sim.engine.Steppable;

import java.util.ArrayList;

/** Class implementing the logarithmic direct method (LDM) SSA. */
public class LogarithmicDirectMethod extends StochasticSimulationAlgorithm {

	/** The nextAction. This needs to be global, so it can be used in the in line Steppable class. */
	private Action nextAction = null;

	public void init(SSASimState model) {
		super.init(model);
		/* Schedule the first step. No initialisation needed. */
		state.schedule.scheduleOnce(0.0, new Steppable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void step(SimState state) {
				LogarithmicDirectMethod.this.step();
			}
		});
	}

	private void step() {
		double rateSum = 0;
		ArrayList<Double> partialSums = new ArrayList<Double>(Action.ActionInstances.size());
		ArrayList<Action> applicableActions = new ArrayList<Action>(Action.ActionInstances.size());
		nextAction = null;
		/* calculate sum of propensities and list of partial sums */
		for (Action action : Action.ActionInstances) {
			if (action.evaluateCondition()) {
				double rate = action.calculateRate();
				if (rate > 0) {
					rateSum += rate;
					partialSums.add(rateSum);
					applicableActions.add(action);
				}
			}
		}
		if (applicableActions.size() > 0) {
			/* draw two random numbers */
			double r1 = state.random.nextDouble();
			double r2 = state.random.nextDouble();
			/* calculate waiting time */
			double waitingTime = (1.0 / rateSum) * Math.log(1.0 / r1);
			/* perform binary search for next action by using the list of partial sums. */
			double propensityLimit = r2 * rateSum;
			int rightBound = partialSums.size() - 1;
			int leftBound = 0;
			int center = 0;
			while (leftBound <= rightBound) {
				center = leftBound + ((rightBound - leftBound) / 2);
				if ((center == 0 && partialSums.get(center) > propensityLimit)
						|| (center > 0 && partialSums.get(center) > propensityLimit
								&& partialSums.get(center - 1) <= propensityLimit)) {
					nextAction = applicableActions.get(center);
					break;
				} else if (partialSums.get(center) > propensityLimit) {
					rightBound = center - 1;
				} else {
					leftBound = center + 1;
				}
			}

			/* schedule next action (if there is one) */
			if (nextAction != null) {
				state.schedule.scheduleOnceIn(waitingTime, new Steppable() {
					private static final long serialVersionUID = 1L;

					@Override
					public void step(SimState state) {
						nextAction.applyEffect();
						LogarithmicDirectMethod.this.step();
					}
				});
			}
		}
	}

}
