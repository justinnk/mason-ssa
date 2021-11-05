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

package org.justinnk.ssamason.test;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.justinnk.ssamason.extension.graphs.ErdosRenyiGraphCreator;
import sim.field.network.Network;
import sim.util.Bag;

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
          fail(
              "[h="
                  + numNodes
                  + ",prob="
                  + prob
                  + "] Expected density ("
                  + expectedDegree
                  + ") differs too much from real density ("
                  + (realDegree)
                  + ")");
        }
      }
    }
  }
}
