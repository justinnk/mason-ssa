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

package org.justinnk.ssamason.demo.mason.vanillasirs;

import org.justinnk.ssamason.demo.InfectionState;
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
