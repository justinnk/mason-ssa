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

package org.justinnk.masonssa.extension;

import org.justinnk.masonssa.extension.ssa.StochasticSimulationAlgorithm;
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
