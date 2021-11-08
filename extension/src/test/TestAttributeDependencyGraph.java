/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.justinnk.ssamason.extension.Action;
import org.justinnk.ssamason.extension.Agent;
import org.justinnk.ssamason.extension.ssa.AttributeDependencyGraph;
import sim.engine.SimState;

class TestAttributeDependencyGraph {

  static class A extends Agent {
    public int a;
    public int b;

    public A(SimState model) {
      super(model);
      this.addAction(new Action(null, null, null, "1"));
      this.addAction(new Action(null, null, null, "2"));
    }
  }

  static class B extends Agent {
    public int a;

    public B(SimState model) {
      super(model);
      this.addAction(new Action(null, null, null, "1"));
    }
  }

  AttributeDependencyGraph graph;

  A a1;
  A a2;
  B b1;

  @BeforeEach
  void setUp() {

    a1 = new A(null);
    a2 = new A(null);
    b1 = new B(null);

    graph = new AttributeDependencyGraph();

    graph.addDependency(a1.getActions().get(0), "a", a1);
    graph.addDependency(a1.getActions().get(0), "b", a1);
    graph.addDependency(a1.getActions().get(1), "a", b1);
    graph.addDependency(a1.getActions().get(0), "a", a1);

    graph.addDependency(a2.getActions().get(0), "a", a2);
    graph.addDependency(a2.getActions().get(0), "b", a2);
    graph.addDependency(a2.getActions().get(1), "a", b1);
    graph.addDependency(a2.getActions().get(0), "a", a2);

    graph.addDependency(b1.getActions().get(0), "a", a1);
    graph.addDependency(b1.getActions().get(0), "a", b1);
  }

  @Test
  void testDependencyRemoval1() {

    graph.RemoveDependencies(a1);

    assertTrue(graph.GetDependents("a", a1).isEmpty());
    assertTrue(graph.GetDependents("b", a1).isEmpty());

    graph.RemoveDependencies(a2);

    assertTrue(graph.GetDependents("a", a2).isEmpty());
    assertTrue(graph.GetDependents("b", a2).isEmpty());

    graph.RemoveDependencies(b1);

    assertTrue(graph.GetDependents("a", b1).isEmpty());
  }
}
