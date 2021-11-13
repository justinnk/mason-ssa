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

package org.justinnk.masonssa.evaluation.correctness;

import java.lang.reflect.InvocationTargetException;
import org.justinnk.masonssa.evaluation.DataFrame;
import sim.engine.SimState;
import sim.engine.Steppable;

public class PredatorPreyModelObserver implements Steppable {
  private static final long serialVersionUID = 1L;
  public DataFrame data = new DataFrame();
  //	private double stepSize = 1.0;

  public PredatorPreyModelObserver(double stepSize) {
    //		this.stepSize = stepSize;
  }

  @Override
  public void step(SimState state) {
    //		int currTime = (int) (state.schedule.getTime() * (1.0 / this.stepSize));
    try {
      int sheep = (int) state.getClass().getMethod("getNumSheep").invoke(state);
      int wolf = (int) state.getClass().getMethod("getNumWolves").invoke(state);
      // data.compute("sheep", currTime, (k, v) -> v == null ? sheep : (double) v + sheep);
      // data.compute("wolf", currTime, (k, v) -> v == null ? wolf : (double) v + wolf);
      data.addEntry("sheep", sheep);
      data.addEntry("wolf", wolf);
    } catch (IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException e) {
      e.printStackTrace();
    }
  }
}
