/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.extension.ssa;

import org.justinnk.ssamason.extension.Action;
import org.justinnk.ssamason.extension.SSASimState;
import sim.engine.SimState;
import sim.engine.Steppable;

/** Class implementing the first reaction method (FRM) SSA. */
public class FirstReactionMethod extends StochasticSimulationAlgorithm {

  /** The nextAction. This needs to be global, so it can be used in the in line Steppable class. */
  private Action nextAction = null;

  @Override
  public void init(SSASimState model) {
    super.init(model);
    /* Schedule the first step. No initialisation needed. */
    state.schedule.scheduleOnce(
        0.0,
        new Steppable() {
          private static final long serialVersionUID = 1L;

          @Override
          public void step(SimState state) {
            FirstReactionMethod.this.step();
          }
        });
  }

  public void step() {
    double minWaitingTime = Double.POSITIVE_INFINITY;
    nextAction = null;
    /* select applicable action with smallest waitingTime */
    for (Action action : Action.ActionInstances) {
      if (action.evaluateCondition()) {
        double rate = action.calculateRate();
        if (rate > 0) {
          double waitingTime = (1.0 / rate) * Math.log(1.0 / state.random.nextDouble());
          if (waitingTime < minWaitingTime) {
            minWaitingTime = waitingTime;
            nextAction = action;
          }
        }
      }
    }
    /* schedule next action */
    if (minWaitingTime != Double.POSITIVE_INFINITY) {
      state.schedule.scheduleOnceIn(
          minWaitingTime,
          new Steppable() {
            private static final long serialVersionUID = 1L;

            @Override
            public void step(SimState state) {
              nextAction.applyEffect();
              FirstReactionMethod.this.step();
            }
          });
    }
  }
}
