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

package org.justinnk.masonssa.extension.graphs;

import sim.field.network.Network;

/** A base class for algorithms that generate (random) graphs based on a MASON network. */
public abstract class GraphCreator {

  /** The seed to use during creation in case the graph is random. */
  protected long seed;

  /**
   * Create a new graph-factory that bases its graphs on a seed. It will always create the same
   * graph given the same seed.
   *
   * @param seed The seed to use for the random graph.
   */
  public GraphCreator(long seed) {
    this.seed = seed;
  }

  /**
   * Connect the nodes found in network to form a graph.
   *
   * @param network The network of which the nodes are to be connected.
   */
  public abstract void create(Network network);
}
