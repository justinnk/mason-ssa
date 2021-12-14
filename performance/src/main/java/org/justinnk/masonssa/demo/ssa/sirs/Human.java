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

package org.justinnk.masonssa.demo.ssa.sirs;

import org.justinnk.masonssa.demo.InfectionState;
import org.justinnk.masonssa.extension.Action;
import org.justinnk.masonssa.extension.Agent;
import sim.engine.SimState;
import sim.field.network.Edge;
import sim.util.Bag;

public class Human extends Agent {

  public InfectionState infectionState = InfectionState.SUSCEPTIBLE;

  public Human(SimState model) {
    super(model);

    SirsModel state = (SirsModel) model;

    /*
     * Get infected if susceptible with a rate proportional to the number of
     * infected neighbours.
     */
    this.addAction(
        new Action(
            () -> this.infectionState == InfectionState.SUSCEPTIBLE,
            () -> {
              // log("caught infection at " + model.schedule.getTime());
              this.infectionState = InfectionState.INFECTIOUS;
            },
            () -> {
              // log("infectious neighbours: " + infectiousNeighbours);
              /* TODO: prohibit division by 0 */
              return state.getInfectionRate() * getInfectiousNeighbours();
            },
            "Infection Event"));

    /* Recover at a certain rate if infected. */
    this.addAction(
        new Action(
            () -> this.infectionState == InfectionState.INFECTIOUS,
            () -> {
              // log("recovered at " + model.schedule.getTime());
              this.infectionState = InfectionState.RECOVERED;
            },
            () -> state.getRecoveryRate(),
            "Recovery Event"));

    this.addAction(
        new Action(
            () -> this.infectionState == InfectionState.RECOVERED,
            () -> this.infectionState = InfectionState.SUSCEPTIBLE,
            () -> state.getLoseImmunityRate(),
            "Lose Immunity Event"));

    this.addAction(
        new Action(
            () -> this.infectionState == InfectionState.SUSCEPTIBLE,
            () -> this.infectionState = InfectionState.INFECTIOUS,
            () -> state.getSpontaneousInfectionTime(),
            "Spontaneous Infection Event"));
  }

  /** Retrieve the number of infectious neighbours. */
  private int getInfectiousNeighbours() {
    Bag contacts = ((SirsModel) model).contacts.getEdges(this, null);
    int numContacts = contacts.size();
    int numInfectedContacts = 0;
    for (int contact = 0; contact < numContacts; contact++) {
      Human contactPerson = (Human) ((Edge) contacts.get(contact)).getOtherNode(this);
      InfectionState contactState = contactPerson.infectionState;
      if (contactState == InfectionState.INFECTIOUS) // && this != contactPerson)
      {
        numInfectedContacts += 1;
      }
    }
    return numInfectedContacts;
  }
}
