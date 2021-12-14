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

import java.util.List;
import java.util.stream.Collectors;
import org.justinnk.masonssa.extension.Action;
import org.justinnk.masonssa.extension.Agent;
import org.justinnk.masonssa.extension.NoAgentAttribute;
import sim.engine.SimState;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;

public class Species extends Agent {

  @NoAgentAttribute public static final int maxFriends = 2;

  protected int getFriendRadius() {
    return 25;
  }

  public Species(SimState model, Network ownContacts) {
    super(model);

    PredatorPreyModel state = (PredatorPreyModel) model;

    this.addAction(
        new Action(
            () -> ownContacts.getEdges(this, null).size() < maxFriends,
            () -> {
              log("Contacts");
              //					Bag allSpecies = new Bag(ownContacts.getAllNodes());
              //					Bag existingContacts = ownContacts.getEdges(this, null);
              //					/* prevent existing contacts and self from being picked as friend */
              //					for (int i = 0; i < existingContacts.size(); i++)
              //					{
              //						Species existing =
              // (Species)((Edge)existingContacts.get(i)).getOtherNode(this);
              //						allSpecies.remove(existing);
              //					}
              //					allSpecies.remove(this);
              /* add a new friend if possible */
              Species friend = pickFriendInCircumference(ownContacts);
              if (friend != null) {
                ownContacts.addEdge(this, friend, 1.0);
              }
            },
            () -> {
              return state.contact;
            },
            "Contact"));
  }

  @SuppressWarnings("unchecked")
  private Species pickFriendInCircumference(Network contacts) {
    PredatorPreyModel state = (PredatorPreyModel) model;
    /* query world for neighbours within distance */
    Bag neighbours =
        new Bag(
            state.world.getNeighborsExactlyWithinDistance(
                state.world.getObjectLocation(this), this.getFriendRadius()));
    /* get the existing connections */
    Bag existingConnections = new Bag(contacts.getEdges(this, null));
    /* retrieve the list of known friends */
    List<Species> alreadyBefriended =
        (List<Species>)
            existingConnections.stream()
                .map(e -> ((Edge) e).getOtherNode(this))
                .collect(Collectors.toList());
    /* make sure the friend is of the same species and unknown */
    List<Species> potentialFriends =
        (List<Species>)
            neighbours.stream()
                .filter(s -> s.getClass() == this.getClass() && !alreadyBefriended.contains(s))
                .collect(Collectors.toList());
    if (!potentialFriends.isEmpty()) {
      /* pick a random neighbour as new friend */
      return (Species) potentialFriends.get(state.random.nextInt(potentialFriends.size()));
    }
    /* if no friend was found */
    return null;
  }

  //	private Species pickRandomSpecies(Bag allSpecies) {
  //		if (allSpecies.size() > 0) {
  //			return (Species) allSpecies.get(model.random.nextInt(allSpecies.size()));
  //		} else {
  //			return null;
  //		}
  //	}
}
