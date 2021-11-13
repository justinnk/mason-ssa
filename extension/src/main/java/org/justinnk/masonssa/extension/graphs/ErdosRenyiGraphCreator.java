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

import ec.util.MersenneTwisterFast;
import sim.field.network.Network;
import sim.util.Bag;

/** This GraphCreator creates an Erdos-Renyi random-graph. */
public class ErdosRenyiGraphCreator extends GraphCreator {

  /** The uniform probability for each edge to exist. */
  private final double probability;
  /** The random generator used. */
  MersenneTwisterFast random;

  /**
   * Create a new ErdosRenyiGraphCreator instance.
   *
   * @param seed The seed for the random generator.
   * @param probability The probability for each edge to exist.
   */
  public ErdosRenyiGraphCreator(long seed, double probability) {
    super(seed);
    this.probability = probability;
    this.random = new MersenneTwisterFast(seed);
    /* prime generator */
    for (int i = 0; i < 624 * 2 + 1; i++) {
      random.nextInt();
    }
  }

  /**
   * Iterate over all possible edges, adding a potential edge to the graph with the uniform
   * probability specified in the constructor. Edges connecting nodes to themselves are not
   * considered. <br>
   * <b>Important Note 1</b>: This is for use with an undirected graph, where the direction of edges
   * does not matter. <br>
   * <b>Important Note 2</b>: All weights are set to 1.0
   *
   * @param network The network to build the graph with. Has to already contain the nodes.
   */
  @Override
  public void create(Network network) {
    random.setSeed(this.seed);
    Bag nodes = network.getAllNodes();
    int size = nodes.size();
    for (int i = 0; i < size; i++) {
      for (int j = i; j < size; j++) {
        if (i != j && random.nextDouble() <= this.probability) {
          Object nodeA = nodes.get(i);
          Object nodeB = nodes.get(j);
          network.addEdge(nodeA, nodeB, 1.0);
        }
      }
    }
  }
}
