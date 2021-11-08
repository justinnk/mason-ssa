/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.demo.mason.vanillasirs;

import org.justinnk.ssamason.demo.InfectionState;
import org.justinnk.ssamason.extension.graphs.ErdosRenyiGraphCreator;
import org.justinnk.ssamason.extension.graphs.GraphCreator;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.Interval;

public class SirsModel extends SimState {

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

  private int initialInfected = 1;

  public int getInitialInfected() {
    return initialInfected;
  }

  public void setInitialInfected(int value) {
    initialInfected = value;
  }

  public Object domInitialInfected() {
    return new Interval(0, numHumans);
  }

  private int initialRecovered = 45;

  public int getInitialRecovered() {
    return initialRecovered;
  }

  public void setInitialRecovered(int value) {
    initialRecovered = value;
  }

  private double recoveryTime = 20.0;

  public double getRecoveryRate() {
    return 1.0 / recoveryTime;
  }

  private double infectionTime = 5.0;

  public double getInfectionRate() {
    return 1.0 / infectionTime;
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
    super(seed);
    this.graph = new ErdosRenyiGraphCreator(42, 0.2);
  }

  public SirsModel(long seed, GraphCreator graph) {
    super(seed);
    this.graph = graph;
  }

  public void start() {
    super.start();
    world.clear();
    contacts.clear();
    // MersenneTwisterFast lrandom = new MersenneTwisterFast(42);
    for (int i = 0; i < numHumans; i++) {
      Human h = new Human(this, i);
      /* Set initial infection */
      if (i < initialInfected) {
        h.infectionState = InfectionState.INFECTIOUS;
      } else if (i < initialInfected + initialRecovered) {
        h.infectionState = InfectionState.RECOVERED;
      }
      /* Place nodes in a grid structure*/
      int rows = (int) Math.sqrt(numHumans);
      world.setObjectLocation(
          h,
          new Double2D(
              world.getWidth() * 0.1 + (int) (i / rows) * 8.0,
              world.getHeight() * 0.1 + (i % rows) * 8.0));
      contacts.addNode(h);
      /* Add node to the schedule */
      schedule.scheduleOnce(
          0.0,
          new Steppable() {
            private static final long serialVersionUID = 1L;

            @Override
            public void step(SimState arg0) {
              h.scheduleNextEvent();
            }
          });
    }
    graph.create(contacts);
  }

  @Override
  public void finish() {
    super.finish();
  }
}
