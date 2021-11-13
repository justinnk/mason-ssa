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

package org.justinnk.masonssa.extension;

import java.util.ArrayList;
import java.util.List;
import sim.engine.TentativeStep;

/**
 * A class representing a single behaviour rule for an agent consisting of a guard expression, an
 * effect and a rate.
 */
public class Action {

  /**
   * Holds a list of all actions created. This list has to be cleared in the SimStates finish()
   * method when using the GUI.
   */
  public static List<Action> ActionInstances = new ArrayList<>();
  /** Counts the number of actions to give them unique names if no name is supplied. */
  public static int ActionInstanceCounter = 0;

  /** The guard expression for this action. */
  private Guard guard = null;
  /** The function this action executes when activated. */
  private Effect effect = null;
  /** The rate function that governs the sojourn time between executions of this action. */
  private RateExpression rate = null;
  /** A name for easy identification. */
  private String name = "";
  /** The owner of this action. */
  private Agent owner = null;
  /**
   * The value of the rate expression when it was evaluated last. This is used for calculating the
   * new rate in the NRM.
   */
  private double currentRate = 0.0;
  /**
   * The value of the guard expression when it was evaluated last. This is used for recalculating
   * the rate sum in the ODM.
   */
  private boolean currentGuard = false;
  /**
   * The time stamp resulting from the last evaluation of the rate expression. This is used for
   * calculating the new rate in the NRM.
   */
  private double currentTimestamp = Double.NEGATIVE_INFINITY;
  /**
   * The currently scheduled event for this action. This is needs to be tracked for stopping the
   * last event when rescheduling in the NRM.
   */
  private TentativeStep nextEvent = null;

  /**
   * Define a new behaviour rule for an agents. Should not be instantiated outside of a class
   * inheriting from agent.
   *
   * @param precondition The guard function for determining whether this action is applicable in the
   *     current simulation state.
   * @param effect The effect function that is executed when the action is applied.
   * @param rate The rate of this action governing the sojourn time between applications.
   */
  public Action(Guard precondition, Effect effect, RateExpression rate) {
    init(precondition, effect, rate);
  }

  /**
   * Define a new behaviour rule for an agents. Should not be instantiated outside of a class
   * inheriting from agent.
   *
   * @param precondition The guard function for determining whether this action is applicable in the
   *     current simulation state.
   * @param effect The effect function that is executed when the action is applied.
   * @param rate The rate of this action governing the sojourn time between applications.
   * @param name The name of this action. Must be unique within an agent.
   */
  public Action(Guard precondition, Effect effect, RateExpression rate, String name) {
    init(precondition, effect, rate);
    this.name = name;
  }

  /**
   * Internal initialisation routine that sets the name properly and also adds this instance to the
   * instance list.
   */
  private void init(Guard precondition, Effect effect, RateExpression rate) {
    this.guard = precondition;
    this.effect = effect;
    this.rate = rate;
    this.name = "Action" + ActionInstanceCounter;
    ActionInstanceCounter++;
    ActionInstances.add(this);
  }

  /** Replace the next event by newNextAction. Update the currentTimestamp. */
  public void resetNextEvent(TentativeStep newNextAction, double timestamp) {
    if (this.nextEvent != null) {
      this.nextEvent.stop();
    }
    this.nextEvent = newNextAction;
    this.currentTimestamp = timestamp;
  }

  /**
   * If there is an event scheduled for this action, stop it. Resets the currentTimestamp to
   * Double.NETGATIVE_INFINITY and currentRate to 0.0
   */
  public void stopNextEvent() {
    if (this.nextEvent != null) {
      this.nextEvent.stop();
    }
    this.currentTimestamp = Double.NEGATIVE_INFINITY;
    this.currentRate = 0.0;
  }

  /** Set the agent that defines this action as owner. */
  public void setOwner(Agent owner) {
    this.owner = owner;
  }

  /** @return The owner of this action. */
  public Agent getOwner() {
    return this.owner;
  }

  /** @return The name of this action. */
  public String getName() {
    return this.name;
  }

  /**
   * Evaluate the guard expression for this action in the current simulation state. This will also
   * update the currentGuard to the new value.
   */
  public boolean evaluateCondition() {
    boolean condition = this.guard.evaluate();
    this.currentGuard = condition;
    return condition;
  }

  /**
   * Calculate the rate function for this action in the current simulation state. This will also
   * update the currentRate to the new value.
   */
  public double calculateRate() {
    double rate = this.rate.rate();
    this.currentRate = rate;
    return rate;
  }

  /** Execute the effect of this action. */
  public void applyEffect() {
    this.effect.apply();
  }

  /**
   * The value of the rate expression when it was evaluated last. This is used for calculating the
   * new rate in the NRM.
   */
  public double getCurrentRate() {
    return this.currentRate;
  }

  /**
   * The value of the guard expression when it was evaluated last. This is used for recalculating
   * the rate sum in the ODM.
   */
  public boolean getCurrentGuard() {
    return this.currentGuard;
  }

  /**
   * The time stamp resulting from the last evaluation of the rate expression. This is used for
   * calculating the new rate in the NRM.
   */
  public double getCurrentTimestamp() {
    return this.currentTimestamp;
  }

  /** Converts this action to a human readable string for debugging. */
  public String toString() {
    return "Agent " + this.owner + "s " + this.name;
  }

  /*
   * For determining unique actions and using hash sets (generated by Eclipse
   * IDE). Actions can be uniquely identified by their name and the id of their
   * owner.
   */

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Action other = (Action) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (owner == null) {
      return other.owner == null;
    } else return owner.equals(other.owner);
  }
}
