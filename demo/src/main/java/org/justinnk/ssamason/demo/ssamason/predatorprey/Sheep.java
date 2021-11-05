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

package org.justinnk.ssamason.demo.ssamason.predatorprey;

import org.justinnk.ssamason.extension.Action;
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
