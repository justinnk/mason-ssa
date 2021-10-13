package org.justinnk.ssamason.extension.graphs;

import sim.field.network.Network;

/**
 * A base class for algorithms that generate (random) graphs based on a MASON
 * network.
 */
public abstract class GraphCreator {

	/** The seed to use during creation in case the graph is random. */
	protected long seed;

	/**
	 * Create a new graph-factory that bases its graphs on a seed. It will always
	 * create the same graph given the same seed.
	 * 
	 * @param seed The seed to use for the random graph.
	 */
	public GraphCreator(long seed) {
		this.seed = seed;
	}

	/** Connect the nodes found in network to form a graph. */
	public abstract void create(Network network);
}
