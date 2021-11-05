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

import org.justinnk.ssamason.extension.SSASimState;

/** Base class for all SSAs that use dependencies to optimise their performance. */
public class DependencyBasedSSA extends StochasticSimulationAlgorithm {

  /** The action-attribute dependency relations. */
  public AttributeDependencyGraph attributeDependencies;
  /** The action-edge dependency relations. */
  public EdgeDependencyGraph edgeDependencies;

  /** Create a new instance of this dependency-based SSA, initialising the dependency graphs. */
  public DependencyBasedSSA() {
    super();
    this.attributeDependencies = new AttributeDependencyGraph();
    this.edgeDependencies = new EdgeDependencyGraph();
  }

  @Override
  public void init(SSASimState model) {
    super.init(model);
    /* Important: clear the dependencies when the simulation is rerun. */
    attributeDependencies.clearAllDependencies();
    edgeDependencies.clearAllDependencies();
  }
}
