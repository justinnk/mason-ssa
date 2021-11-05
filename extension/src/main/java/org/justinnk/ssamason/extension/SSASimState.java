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

package org.justinnk.ssamason.extension;

import org.justinnk.ssamason.extension.ssa.StochasticSimulationAlgorithm;
import sim.engine.SimState;
import sim.engine.Steppable;

/** Base class for all MASON simulations that use the SSA extension. Replaces SimState. */
public class SSASimState extends SimState {

  private static final long serialVersionUID = 1L;
  /** The SSA to use. */
  protected StochasticSimulationAlgorithm simulator;

  /** Create a new model instance using the given seed and simulator. */
  public SSASimState(long seed, StochasticSimulationAlgorithm simulator) {
    super(seed);
    this.simulator = simulator;
  }

  /** Initialise the model for a new replication. */
  @Override
  public void start() {
    super.start();
    /* clear the static state. */
    Action.ActionInstances.clear();
    Action.ActionInstanceCounter = 0;
    //		Agent.AgentInstances.clear();
    Agent.InternalNumAgents = 0;
    /* make simulator initialisation the first step. */
    schedule.scheduleOnce(
        0.0,
        new Steppable() {
          private static final long serialVersionUID = 1L;

          @Override
          public void step(SimState state) {
            simulator.init(SSASimState.this);
          }
        });
  }

  /** Clean up after the replication. */
  @Override
  public void finish() {
    super.finish();
    /* clear the static state. */
    Action.ActionInstances.clear();
    Action.ActionInstanceCounter = 0;
    //		Agent.AgentInstances.clear();
    Agent.InternalNumAgents = 0;
  }
}
