/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.extension.ssa;

import org.justinnk.masonssa.extension.SSASimState;

/** Interface providing common methods of stochastic simulation algorithms. */
public abstract class StochasticSimulationAlgorithm {

  /** The SSAs view of the current simulation state. */
  protected SSASimState state;

  public StochasticSimulationAlgorithm() {}

  /**
   * Initialise the SSA.
   *
   * @param model The initial model state.
   */
  public void init(SSASimState model) {
    this.state = model;
  }
}
