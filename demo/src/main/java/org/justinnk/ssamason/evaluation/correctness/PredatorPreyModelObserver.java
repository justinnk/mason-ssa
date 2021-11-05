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
