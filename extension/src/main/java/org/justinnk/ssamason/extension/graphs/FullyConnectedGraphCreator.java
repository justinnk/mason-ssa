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

package org.justinnk.ssamason.extension.graphs;

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
