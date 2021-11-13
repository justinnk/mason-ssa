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
import sim.util.Bag;

/** This GraphCreator connects all the nodes with each other node in the network. */
public class FullyConnectedGraphCreator extends GraphCreator {

  /** Create a new FullyConnectedGraphCreator instance. */
  public FullyConnectedGraphCreator() {
    /* The seed does not matter, as this creator is deterministic. */
    super(42);
  }

  /**
   * Connect all the nodes with each other node (except themselves). <br>
   * <b>Important Note 1</b>: This is for use with an undirected graph, where the direction of edges
   * does not matter. <br>
   * <b>Important Note 2</b>: All weights are set to 1.0
   *
   * @param network The network to build the graph with. Has to already contain the nodes.
   */
  @Override
  public void create(Network network) {
    Bag nodes = network.getAllNodes();
    int size = nodes.size();
    for (int i = 0; i < size; i++) {
      Object nodeA = nodes.get(i);
      for (int j = i; j < size; j++) {
        Object nodeB = nodes.get(j);
        if (i != j) {
          network.addEdge(nodeA, nodeB, 1.0);
        }
      }
    }
  }
}
