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

package org.justinnk.ssamason.demo.mason.students;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Edge;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

public class Student implements Steppable {

  /** */
  private static final long serialVersionUID = 1L;

  public static final double MAX_FORCE = 3.0;

  private double friendsClose = 0.0;
  private double enemiesCloser = 10.0;

  public double getAgitation() {
    return friendsClose + enemiesCloser;
  }

  public void step(SimState state) {
    Students students = (Students) state;
    Continuous2D yard = students.yard;

    Double2D me = students.yard.getObjectLocation(this);

    MutableDouble2D sumForces = new MutableDouble2D();

    friendsClose = 0.0;
    enemiesCloser = 0.0;

    // Go through my buddies and determine how much I want to be near them
    MutableDouble2D forceVector = new MutableDouble2D();
    Bag out = students.buddies.getEdges(this, null);
    int len = out.size();
    for (int buddy = 0; buddy < len; buddy++) {
      Edge e = (Edge) (out.get(buddy));
      double buddiness = (Double) (e.info);

      // I could be in the to() end or the from() end. getOtherNode is a cute function
      // which grabs the guy at the opposite end from me.
      Double2D him = students.yard.getObjectLocation(e.getOtherNode(this));
      if (buddiness >= 0) // the further I am from him the more I want to go to him
      {
        forceVector.setTo((him.x - me.x) * buddiness, (him.y - me.y) * buddiness);
        if (forceVector.length() > MAX_FORCE) // I'm far enough away
        {
          forceVector.resize(MAX_FORCE);
        }
        friendsClose += forceVector.length();
      } else // the nearer I am to him the more I want to get away from him, up to a limit
      {
        forceVector.setTo((him.x - me.x) * buddiness, (him.y - me.y) * buddiness);
        if (forceVector.length() > MAX_FORCE) // I'm far enough away
        {
          forceVector.resize(0.0);
        } else if (forceVector.length() > 0) {
          forceVector.resize(MAX_FORCE - forceVector.length()); // invert the distance
          enemiesCloser += forceVector.length();
        }
      }
      sumForces.addIn(forceVector);
    }

    sumForces.addIn(
        new Double2D(
            (yard.width * 0.5 - me.x) * students.forceToSchoolMultiplier,
            (yard.height * 0.5 - me.y) * students.forceToSchoolMultiplier));

    sumForces.addIn(
        new Double2D(
            students.randomMultiplier * (students.random.nextDouble() - 0.5),
            students.randomMultiplier * (students.random.nextDouble() - 0.5)));

    sumForces.addIn(me);

    students.yard.setObjectLocation(this, new Double2D(sumForces));
  }
}
