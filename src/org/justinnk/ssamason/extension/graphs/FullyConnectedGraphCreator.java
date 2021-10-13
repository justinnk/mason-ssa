package org.justinnk.ssamason.extension.graphs;

import sim.field.network.Network;
import sim.util.Bag;

/**
 * This GraphCreator connects all the nodes with each other node in the network.
 */
public class FullyConnectedGraphCreator extends GraphCreator {

	/** Create a new FullyConnectedGraphCreator instance. */
	public FullyConnectedGraphCreator() {
		/* The seed does not matter, as this creator is deterministic. */
		super(42);
	}

	/**
	 * Connect all the nodes with each other node (except themselves). <br>
	 * <b>Important Note 1</b>: This is for use with an undirected graph, where the
	 * direction of edges does not matter. <br>
	 * <b>Important Note 2</b>: All weights are set to 1.0
	 * 
	 * @param network The network to build the graph with. Has to already contain
	 *                the nodes.
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
