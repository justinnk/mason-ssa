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

package org.justinnk.masonssa.extension.ssa;

import org.justinnk.masonssa.extension.Action;
import org.justinnk.masonssa.extension.SSASimState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.TentativeStep;

/**
 * Class implementing an aspect-oriented next reaction method (AO-NRM) SSA. This handles the basic
 * algorithm structure. The rest of the implementation is done by DependencySymbiont and
 * NRMDependencySymbiont.
 */
public class NextReactionMethod extends DependencyBasedSSA {

  public NextReactionMethod() {
    super();
  }

  @Override
  public void init(SSASimState model) {
    super.init(model);
    /* schedule all events (and build dependency graph) */
    for (Action action : Action.ActionInstances) {
      initAction(action);
    }
    //		attributeDependencies.printActionDependencies();
    //		edgeDependencies.printActionDependencies();
  }

  /**
   * Initialise a an action. This will recalculate its rate and guard and schedule its next event if
   * the action is applicable.
   *
   * @param action The action to initialise.
   */
  public void initAction(Action action) {
    if (action.evaluateCondition()) {
      double rate = action.calculateRate();
      if (rate > 0) {
        double waitingTime = expDist(rate);
        TentativeStep step =
            new TentativeStep(
                new Steppable() {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void step(SimState state) {
                    executeAction(action);
                  }
                });
        double currentTime = state.schedule.getTime();
        state.schedule.scheduleOnceIn(waitingTime, step);
        action.resetNextEvent(step, waitingTime + currentTime);
      }
    }
  }

  /** Draw an exponentially distributed random number. */
  private double expDist(double lambda) {
    return (1.0 / lambda) * Math.log(1.0 / state.random.nextDouble());
  }

  /**
   * Apply an actions effect (if it is not null).
   *
   * @param action The action, whose effect is applied.
   */
  public void executeAction(Action action) {
    if (action != null) {
      action.applyEffect();
    }
  }

  /**
   * Reschedule an action because it is affected by the action trigger. Draw a new random number
   * only if the action to reschedule is the trigger.
   *
   * @param action The action to reschedule.
   * @param trigger The action that triggered the rescheduling.
   */
  public void rescheduleAction(Action action, Action trigger) {
    //		assert (trigger != null);
    if (action.evaluateCondition()) {
      /* Rescue the old rate and calculate the new rate. */
      double oldRate = action.getCurrentRate();
      double rate = action.calculateRate();
      if (rate > 0) {
        /* Prepare the step that will be scheduled. */
        double currentTime = state.schedule.getTime();
        TentativeStep step =
            new TentativeStep(
                new Steppable() {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void step(SimState state) {
                    executeAction(action);
                  }
                });
        /*
         * Is the current action the trigger of the rescheduling OR was this action not
         * applicable during initialisation?
         */
        if ((trigger.equals(action)) || action.getCurrentTimestamp() == Double.NEGATIVE_INFINITY) {
          /* If yes, draw new random number */
          double waitingTime = expDist(rate);
          state.schedule.scheduleOnceIn(waitingTime, step);
          action.resetNextEvent(step, currentTime + waitingTime);
        } else {
          /* Otherwise update waiting time */
          /*
           * Note: oldTimestamp will always be NEGATIVE_INFINITY if oldRate is 0, because
           * of stopNextEvent.
           */
          double waitingTime = (oldRate / rate) * (action.getCurrentTimestamp() - currentTime);
          //					double waitingTime = expDist(rate);
          state.schedule.scheduleOnceIn(waitingTime, step);
          action.resetNextEvent(step, currentTime + waitingTime);
        }
      } else {
        action.stopNextEvent();
      }
    } else {
      action.stopNextEvent();
    }
  }
}
