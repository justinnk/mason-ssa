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

package org.justinnk.ssamason.evaluation;

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
