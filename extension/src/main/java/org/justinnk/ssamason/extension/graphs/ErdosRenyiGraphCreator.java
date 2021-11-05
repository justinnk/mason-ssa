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
