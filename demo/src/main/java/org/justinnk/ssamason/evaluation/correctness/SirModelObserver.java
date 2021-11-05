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

package org.justinnk.ssamason.evaluation.correctness;

import java.lang.reflect.InvocationTargetException;
import org.justinnk.ssamason.evaluation.DataFrame;
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
