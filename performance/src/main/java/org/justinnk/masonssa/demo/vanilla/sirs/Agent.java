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

import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.TentativeStep;

public class Agent {

  protected SimState model;

  private HashMap<String, Steppable> actions = new HashMap<>();

  protected void addAction(String name, Steppable action) {
    actions.put(name, action);
  }

  protected TentativeStep currentAction;

  protected void scheduleActionOnceIn(double delta, String actionName) {
    currentAction = new TentativeStep(actions.get(actionName));
    model.schedule.scheduleOnceIn(delta, currentAction);
  }

  private int id;

  public int getId() {
    return id;
  }

  public Agent(SimState model, int id) {
    this.model = model;
    this.id = id;
  }

  protected void log(String message) {
    System.out.println(id + ": " + message);
  }

  @Override
  public String toString() {
    return String.valueOf(id);
  }
}
