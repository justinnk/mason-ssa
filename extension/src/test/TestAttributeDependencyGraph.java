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

package org.justinnk.masonssa.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.justinnk.masonssa.extension.Action;
import org.justinnk.masonssa.extension.Agent;
import org.justinnk.masonssa.extension.ssa.AttributeDependencyGraph;
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
