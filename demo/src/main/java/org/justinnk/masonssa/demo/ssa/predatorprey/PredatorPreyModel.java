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

package org.justinnk.masonssa.demo.ssa.predatorprey;

import org.justinnk.masonssa.extension.SSASimState;
import org.justinnk.masonssa.extension.graphs.GridGraphCreator;
import org.justinnk.masonssa.extension.ssa.FirstReactionMethod;
import org.justinnk.masonssa.extension.ssa.StochasticSimulationAlgorithm;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.util.Double2D;

public class PredatorPreyModel extends SSASimState {

  private static final long serialVersionUID = 1L;

  public static void main(String[] args) {
    doLoop(PredatorPreyModel.class, args);
    System.exit(0);
  }

  public Continuous2D world = new Continuous2D(1.0, 50.0, 50.0);
  public Network sheep = new Network(false);
  public Network wolves = new Network(false);
  public Network undercoverNet = new Network(false);

  private int numSheep = 10;

  public int getNumSheep() {
    return sheep.getAllNodes().size();
  }

  public void setNumSheep(int value) {
    numSheep = value;
  }

  private int numWolves = 10;

  public int getNumWolves() {
    return wolves.getAllNodes().size();
  }

  public void setNumWolves(int value) {
    numWolves = value;
  }

  public double contact = 1.0;

  public double wolfReproduction = 2.0;
  public double wolfHunger = 0.7;
  public double wolfDie = 1.0;
  public double wolfHunt = 1.0;
  public double wolfGoUndercover = 5.0;

  public double sheepReproduce = 2.0;

  public PredatorPreyModel(long seed) {
    super(seed, new FirstReactionMethod());
  }

  public PredatorPreyModel(long seed, StochasticSimulationAlgorithm simulator) {
    super(seed, simulator);
  }

  public void start() {
    super.start();

    world.clear();
    sheep.clear();
    wolves.clear();
    undercoverNet.clear();

    for (int i = 0; i < numSheep; i++) {
      Sheep s = new Sheep(this);
      /* Place node at random location */
      world.setObjectLocation(s, randomPos());
      sheep.addNode(s);
    }

    GridGraphCreator graph = new GridGraphCreator();
    graph.create(sheep);

    for (int i = 0; i < numWolves; i++) {
      addNewWolf();
      //			Wolf w = new Wolf(this);
      //			/* Place node at random location */
      //			world.setObjectLocation(w, randomPos());
      //			wolves.addNode(w);
    }

    graph.create(wolves);
  }

  public Double2D randomPos() {
    return new Double2D(
        world.getWidth() * 0.5 + (random.nextDouble() - 0.5) * 50.0,
        world.getHeight() * 0.5 + (random.nextDouble() - 0.5) * 50.0);
  }

  public void addNewWolf() {
    Wolf newBorn = new Wolf(this);
    this.world.setObjectLocation(newBorn, this.randomPos());
    this.wolves.addNode(newBorn);
    this.undercoverNet.addNode(newBorn);
  }
}
