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

/**
 * A steppable designed to observe sir models.
 *
 * <p>Note: The models have to provide the methods: int getNumSusceptible(), int getNumInfected()
 * and int getNumRecovered() in order for this Steppable to work correctly.
 */
public class SirModelObserver implements Steppable {

  private static final long serialVersionUID = 1L;
  public DataFrame data = new DataFrame();
  //	private double stepSize = 1.0;
  // public int job = 0;

  public SirModelObserver(double stepSize) {
    //		this.stepSize = stepSize;
  }

  @Override
  public void step(SimState state) {
    //		int currTime = (int) (state.schedule.getTime() * (1.0 / this.stepSize));
    try {
      int sus = (int) state.getClass().getMethod("getNumSusceptible").invoke(state);
      int inf = (int) state.getClass().getMethod("getNumInfected").invoke(state);
      int rec = (int) state.getClass().getMethod("getNumRecovered").invoke(state);
      // data.compute("susceptible", currTime, (k, v) -> v == null ? sus : (double) v + sus);
      // data.compute("infected", currTime, (k, v) -> v == null ? inf : (double) v + inf);
      // data.compute("recovered", currTime, (k, v) -> v == null ? rec : (double) v + rec);
      data.addEntry("susceptible", sus);
      data.addEntry("infected", inf);
      data.addEntry("recovered", rec);

    } catch (IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException e) {
      e.printStackTrace();
    }
  }
}
