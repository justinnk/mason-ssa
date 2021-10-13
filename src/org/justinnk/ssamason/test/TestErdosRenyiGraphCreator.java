package org.justinnk.ssamason.test;

import org.junit.jupiter.api.Test;
import org.justinnk.ssamason.extension.graphs.ErdosRenyiGraphCreator;
import sim.field.network.Network;
import sim.util.Bag;

import static org.junit.jupiter.api.Assertions.fail;

class TestErdosRenyiGraphCreator {

	ErdosRenyiGraphCreator creator;
	Network net;

	long seed = 42;
	int maxNumNodes = 500;
	int maxDeviation = 2;

	@Test
	void test() {
		for (int numNodes = 10; numNodes < 1000; numNodes += 10) {
			net = new Network(false);
			for (int i = 0; i < numNodes; i++) {
				net.addNode(new Object());
			}
			for (double prob = 0.05; prob < 1.0; prob += 0.05) {
				System.out.println("[h=" + numNodes + ",prob=" + prob + "]");
				creator = new ErdosRenyiGraphCreator(seed, prob);
				creator.create(net);
				double expectedDegree = prob * (numNodes - 1.0);
				double totalIn = 0;
				Bag nodes = net.getAllNodes();
				for (int i = 0; i < numNodes; i++) {
					totalIn += net.getEdges(nodes.get(i), null).size();
				}
				double realDegree = (totalIn / (numNodes));
				// System.out.println("p=" + prob + "E[D]=" + expectedDegree + ", D=" +
				// realDegree);
				// System.out.println();
				net.removeAllEdges();
				if (Math.abs(realDegree - expectedDegree) > maxDeviation) {
					fail("[h=" + numNodes + ",prob=" + prob + "] Expected density (" + expectedDegree
							+ ") differs too much from real density (" + (realDegree) + ")");
				}
			}
		}
	}

}
