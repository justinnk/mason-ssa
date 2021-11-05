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
