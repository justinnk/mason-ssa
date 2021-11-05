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

import java.awt.*;
import javax.swing.*;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.network.SimpleEdgePortrayal2D;
import sim.portrayal.network.SpatialNetwork2D;
import sim.portrayal.simple.OvalPortrayal2D;

public class StudentsWithUI extends GUIState {

  public static void main(String[] args) {
    StudentsWithUI vid = new StudentsWithUI();
    Console c = new Console(vid);
    c.setVisible(true);
  }

  public Display2D display;
  public JFrame displayFrame;
  public ContinuousPortrayal2D yardPortrayal = new ContinuousPortrayal2D();
  public NetworkPortrayal2D buddiesPortrayal = new NetworkPortrayal2D();

  public StudentsWithUI() {
    super(new Students(System.currentTimeMillis()));
  }

  public StudentsWithUI(SimState state) {
    super(state);
  }

  public static String getName() {
    return "Student Schoolyard Cliques";
  }

  public void start() {
    super.start();
    setupPortrayals();
  }

  public void load() {
    super.start();
    setupPortrayals();
  }

  public void setupPortrayals() {
    Students students = (Students) state;

    yardPortrayal.setField(students.yard);
    yardPortrayal.setPortrayalForAll(
        new OvalPortrayal2D() {
          private static final long serialVersionUID = 1L;

          public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
            Student student = (Student) object;

            int agitationshade = (int) (student.getAgitation() * 255 / 10.0);
            if (agitationshade > 255) {
              agitationshade = 255;
            }
            paint = new Color(agitationshade, 0, 255 - agitationshade);
            super.draw(object, graphics, info);
          }
        });

    buddiesPortrayal.setField(new SpatialNetwork2D(students.yard, students.buddies));
    buddiesPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());

    display.reset();
    display.setBackground(Color.white);

    display.repaint();
  }

  public void init(Controller c) {
    super.init(c);
    display = new Display2D(600, 600, this);
    display.setClipping(false);

    displayFrame = display.createFrame();
    displayFrame.setTitle("Schoolyard Display");
    c.registerFrame(displayFrame);
    displayFrame.setVisible(true);
    display.attach(buddiesPortrayal, "Buddies");
    display.attach(yardPortrayal, "Yard");
  }

  public void quit() {
    super.quit();
    if (displayFrame != null) {
      displayFrame.dispose();
    }
    displayFrame = null;
    display = null;
  }
}
