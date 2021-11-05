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

package org.justinnk.ssamason.demo.ssamason.sirs;

import org.justinnk.ssamason.demo.InfectionState;
import org.justinnk.ssamason.extension.SSASimState;
import org.justinnk.ssamason.extension.graphs.ErdosRenyiGraphCreator;
import org.justinnk.ssamason.extension.graphs.GraphCreator;
import org.justinnk.ssamason.extension.ssa.FirstReactionMethod;
import org.justinnk.ssamason.extension.ssa.StochasticSimulationAlgorithm;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.Interval;

/* TODO: at the end add serialisability to extension. */

/**
 * Basic network-based susceptible-infectious-recovered-susceptible model using the SSA-extension to
 * MASON.
 */
public class SirsModel extends SSASimState {

  private static final long serialVersionUID = 1L;

  public static void main(String[] args) {
    doLoop(SirsModel.class, args);
    System.exit(0);
  }

  public Continuous2D world = new Continuous2D(1.0, 100.0, 100.0);
  public Network contacts = new Network(false);
  public GraphCreator graph;

  private int numHumans = 100;

  public int getNumHumans() {
    return numHumans;
  }

  public void setNumHumans(int value) {
    numHumans = value;
  }

  private int initialInfected = 45;

  public int getInitialInfected() {
    return initialInfected;
  }

  public void setInitialInfected(int value) {
    initialInfected = value;
  }

  private int initialRecovered = 45;

  public int getInitialRecovered() {
    return initialRecovered;
  }

  public void setInitialRecovered(int value) {
    initialRecovered = value;
  }

  public Object domInitialInfected() {
    return new Interval(0, numHumans);
  }

  private double recoveryTime = 20.0;

  public double getRecoveryRate() {
    return 1.0 / recoveryTime;
  }

  private double infectionTime = 5.0;

  public double getInfectionRate() {
    return 1.0 / infectionTime;
  }

  private double loseImmunityTime = 20.0;

  public double getLoseImmunityRate() {
    return 1.0 / loseImmunityTime;
  }

  private double spontaneousInfectionRate = 200.0;

  public double getSpontaneousInfectionTime() {
    return 1.0 / spontaneousInfectionRate;
  }

  public int getNumSusceptible() {
    Bag humans = contacts.getAllNodes();
    int num = 0;
    for (int i = 0; i < humans.size(); i++) {
      if (((Human) humans.get(i)).infectionState == InfectionState.SUSCEPTIBLE) {
        num += 1;
      }
    }
    return num;
  }

  public int getNumInfected() {
    Bag humans = contacts.getAllNodes();
    int num = 0;
    for (int i = 0; i < humans.size(); i++) {
      if (((Human) humans.get(i)).infectionState == InfectionState.INFECTIOUS) {
        num += 1;
      }
    }
    return num;
  }

  public int getNumRecovered() {
    Bag humans = contacts.getAllNodes();
    int num = 0;
    for (int i = 0; i < humans.size(); i++) {
      if (((Human) humans.get(i)).infectionState == InfectionState.RECOVERED) {
        num += 1;
      }
    }
    return num;
  }

  public SirsModel(long seed) {
    super(seed, new FirstReactionMethod());
    this.graph = new ErdosRenyiGraphCreator(42, 0.2);
  }

  public SirsModel(long seed, StochasticSimulationAlgorithm simulator, GraphCreator graph) {
    super(seed, simulator);
    this.graph = graph;
  }

  public void start() {
    super.start();
    world.clear();
    contacts.clear();
    for (int i = 0; i < numHumans; i++) {
      Human h = new Human(this);
      /* Set initial infection */
      if (i < initialInfected) {
        h.infectionState = InfectionState.INFECTIOUS;
      } else if (i < initialInfected + initialRecovered) {
        h.infectionState = InfectionState.RECOVERED;
      }
      /* Place humans in grid shape */
      int rows = (int) Math.sqrt(numHumans);
      world.setObjectLocation(
          h,
          new Double2D(
              world.getWidth() * 0.1 + (int) (i / rows) * 8.0,
              world.getHeight() * 0.1 + (i % rows) * 8.0));
      contacts.addNode(h);
    }
    graph.create(contacts);
  }

  @Override
  public void finish() {
    super.finish();
  }
}
