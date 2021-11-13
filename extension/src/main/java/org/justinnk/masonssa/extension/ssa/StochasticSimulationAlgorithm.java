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
