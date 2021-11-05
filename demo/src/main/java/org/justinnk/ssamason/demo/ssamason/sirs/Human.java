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
import org.justinnk.ssamason.extension.Action;
import org.justinnk.ssamason.extension.Agent;
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
