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

package org.justinnk.masonssa.evaluation;

import sim.engine.SimState;

/** Base class for all (stochastic) experiments performed on models. */
public abstract class Experiment {

  /** The path prefix for all data generated from experiments. */
  public static String outputPathPrefix = "evaluation/";
  /**
   * A seed used for all experiments. This ensures they can be reproduced in an easy way (by using
   * the same seed)
   */
  public static final long seed = 42;

  /** Build a parameterised model. */
  protected abstract SimState parameterise();

  /** Simulate a model for a number of replications. */
  protected abstract DataFrame[] execute(SimState model, int replications);

  /** Evaluate and store the simulation results. */
  protected abstract void evaluate(DataFrame[] results);

  /** Perform a certain number of replications of this experiment. */
  public void run(int replications) {
    evaluate(execute(parameterise(), replications));
  }
}
