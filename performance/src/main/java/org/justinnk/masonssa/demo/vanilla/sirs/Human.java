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

package org.justinnk.masonssa.demo.vanilla.sirs;

import org.justinnk.masonssa.demo.InfectionState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.util.Bag;

public class Human extends Agent {

  public InfectionState infectionState = InfectionState.SUSCEPTIBLE;

  public Human(SimState model, int id) {
    super(model, id);

    this.addAction(
        "infect",
        new Steppable() {
          private static final long serialVersionUID = 1L;

          @Override
          public void step(SimState model) {
            Human.this.getInfected();
          }
        });

    this.addAction(
        "recover",
        new Steppable() {
          private static final long serialVersionUID = 1L;

          @Override
          public void step(SimState model) {
            Human.this.recover();
          }
        });

    this.addAction(
        "loseImmunity",
        new Steppable() {
          private static final long serialVersionUID = 1L;

          @Override
          public void step(SimState model) {
            Human.this.loseImmunity();
          }
        });
  }

  public void scheduleNextEvent() {
    if (this.infectionState == InfectionState.SUSCEPTIBLE) {
      scheduleInfection();
    } else if (this.infectionState == InfectionState.INFECTIOUS) {
      scheduleRecovery();
    } else if (this.infectionState == InfectionState.RECOVERED) {
      scheduleImmunityLoss();
    } else {
      currentAction = null;
    }
  }

  private void scheduleInfection() {
    double infectiousNeighbours = getInfectiousNeighbours();
    /*
     * if (infectiousNeighbours == 0.0) { currentAction = null; } else {
     */
    double rate = ((SirsModel) model).getInfectionRate() * infectiousNeighbours + (0.005);
    double waitingTime = (1.0 / rate) * Math.log(1.0 / model.random.nextDouble());
    this.scheduleActionOnceIn(waitingTime, "infect");
    // log("scheduled infection at " + (model.schedule.getTime() + waitingTime));
    // }
  }

  private void scheduleImmunityLoss() {
    double rate = 0.05;
    double waitingTime = (1.0 / rate) * Math.log(1.0 / model.random.nextDouble());
    this.scheduleActionOnceIn(waitingTime, "loseImmunity");
  }

  private double getInfectiousNeighbours() {
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

  private void scheduleRecovery() {
    double waitingTime =
        (1.0 / ((SirsModel) model).getRecoveryRate()) * Math.log(1.0 / model.random.nextDouble());
    this.scheduleActionOnceIn(waitingTime, "recover");
    // log("scheduled recovery at " + (model.schedule.getTime() + waitingTime));
  }

  public void getInfected() {
    this.infectionState = InfectionState.INFECTIOUS;
    // log("caught infection at " + model.schedule.getTime());
    scheduleRecovery();
    informNeighbours();
  }

  public void recover() {
    this.infectionState = InfectionState.RECOVERED;
    // log("recovered at " + model.schedule.getTime());
    scheduleImmunityLoss();
    informNeighbours();
  }

  public void loseImmunity() {
    this.infectionState = InfectionState.SUSCEPTIBLE;
    scheduleInfection();
    informNeighbours();
  }

  private void informNeighbours() {
    Bag contacts = ((SirsModel) model).contacts.getEdges(this, null);
    int numContacts = contacts.size();
    for (int contact = 0; contact < numContacts; contact++) {
      Human contactPerson = (Human) ((Edge) contacts.get(contact)).getOtherNode(this);
      /*
       * if (this != contactPerson) {
       */
      contactPerson.rescheduleInfectionEventIfPresent();
      // }
    }
  }

  public void rescheduleInfectionEventIfPresent() {
    // log("informed.");
    if (this.infectionState == InfectionState.SUSCEPTIBLE) {
      if (currentAction != null) {
        currentAction.stop();
        // log("stopped current action.");
      }
      scheduleInfection();
    }
  }
}
