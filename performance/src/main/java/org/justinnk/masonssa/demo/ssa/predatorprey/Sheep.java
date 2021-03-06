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

import org.justinnk.masonssa.extension.Action;
import sim.engine.SimState;

public class Sheep extends Species {

  @Override
  protected int getFriendRadius() {
    return 20;
  }

  public Sheep(SimState model) {
    super(model, ((PredatorPreyModel) model).sheep);

    PredatorPreyModel state = (PredatorPreyModel) model;

    this.addAction(
        new Action(
            () -> state.sheep.getEdges(this, null).size() > 0,
            () -> {
              /* give birth */
              Sheep newBorn = new Sheep(state);
              state.world.setObjectLocation(newBorn, state.randomPos());
              state.sheep.addNode(newBorn);
              log("Gives birth to " + newBorn);
            },
            () -> state.sheepReproduce,
            "Reproduce"));

    //		this.addAction(new Action(
    //				() -> state.sheep.getEdges(this, null).size() < maxFriends,
    //				() -> {
    //					log("Contacts");
    ////					Bag allSheep = new Bag(state.sheep.getAllNodes());
    ////					Bag existingContacts = state.sheep.getEdges(this, null);
    ////					/* prevent existing contacts and self from being picked as friend */
    ////					for (int i = 0; i < existingContacts.size(); i++)
    ////					{
    ////						Sheep existing = (Sheep)((Edge)existingContacts.get(i)).getOtherNode(this);
    ////						allSheep.remove(existing);
    ////					}
    ////					allSheep.remove(this);
    //					/* add a new friend if possible */
    //					Sheep friend = pickSheepInCircumference();
    //					if (friend != null) {
    //						state.sheep.addEdge(this, friend, 1.0);
    //						log("adds friend " + friend);
    //					}
    //				}, () -> {
    //					return state.sheepContact;
    //				}, "Contact"));
  }

  //	private Sheep pickRandomSheep(Bag allSheep) {
  //		if (allSheep.size() > 0) {
  //			return (Sheep) allSheep.get(model.random.nextInt(allSheep.size()));
  //		} else {
  //			return null;
  //		}
  //	}

  //	private Sheep pickSheepInCircumference() {
  //		PredatorPreyModel state = (PredatorPreyModel)model;
  //		assert(state.world.getObjectLocation(this) != null);
  //			Bag neighbors = new
  // Bag(state.world.getNeighborsExactlyWithinDistance(state.world.getObjectLocation(this), 25));
  //			//log(" (pickSheep) has " + neighbors.size());
  //			if (!neighbors.isEmpty()) {
  //				Bag currentObserved = new Bag(state.undercoverNet.getEdges(this, null));
  //				List<Sheep> c = (List<Sheep>) currentObserved.stream().map((e) ->
  // ((Edge)e).getOtherNode(this)).collect(Collectors.toList());
  //				List<Sheep> n = (List<Sheep>) neighbors.stream().filter((s) -> s instanceof Sheep &&
  // !c.contains(s)).collect(Collectors.toList());
  //				if (!n.isEmpty()) {
  //					Sheep pick = (Sheep)n.get(state.random.nextInt(n.size()));
  //					//log("picks " + pick);
  //					return pick;
  //				} else {
  //					return null;
  //				}
  //			} else {
  //				return null;
  //			}
  //	}

}
