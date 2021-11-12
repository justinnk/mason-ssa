/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
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
