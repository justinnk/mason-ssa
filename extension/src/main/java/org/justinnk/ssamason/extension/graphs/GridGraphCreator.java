/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.extension.graphs;

import sim.field.network.Network;
import sim.util.Bag;

/**
 * GraphCreator that creates a grid-like structure, connecting node based on a
 * von-Neumann-neighbourhood.
 */
public class GridGraphCreator extends GraphCreator {

  /** Create a new GirdGraphCreator instance. */
  public GridGraphCreator() {
    /* The seed is not important, as this creator is deterministic. */
    super(42);
  }

  /**
   * Connect the nodes found in network according to a von-Neumann-neighbourhood. <br>
   * <b>Important Note 1</b>: This is for use with an undirected graph, where the direction of edges
   * does not matter. For information: the edges are directed bottom to top and left to right. <br>
   * <b>Important Note 2</b>: All weights are set to 1.0
   *
   * @param network The network to build the graph with. Has to already contain the nodes.
   */
  @Override
  public void create(Network network) {
    Bag nodes = network.getAllNodes();
    int size = nodes.size();
    int rows = (int) Math.sqrt(size);
    for (int i = 0; i < size; i++) {
      Object node = nodes.get(i);
      /* If we are not the first node of this row */
      if (i > 0 && i % rows != 0) {
        /* connect us to our neighbour above */
        Object nodeT = nodes.get(i - 1);
        network.addEdge(node, nodeT, 1.0);
      }
      /* If we are not in the last row */
      if (i + rows < size) {
        /* connect us to our neighbour on the right */
        Object nodeR = nodes.get(i + rows);
        network.addEdge(node, nodeR, 1.0);
      }
    }
  }
}
