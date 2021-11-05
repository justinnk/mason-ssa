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
