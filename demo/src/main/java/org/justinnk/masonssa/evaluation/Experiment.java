/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
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
