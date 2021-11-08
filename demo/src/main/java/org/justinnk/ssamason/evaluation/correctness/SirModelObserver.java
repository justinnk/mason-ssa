/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
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
