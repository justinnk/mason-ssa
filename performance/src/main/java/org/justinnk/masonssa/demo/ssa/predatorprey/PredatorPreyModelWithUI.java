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

package org.justinnk.masonssa.demo.ssa.predatorprey;

import java.awt.*;
import javax.swing.*;
import org.justinnk.masonssa.extension.ssa.FirstReactionMethod;
import sim.display.*;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.network.SimpleEdgePortrayal2D;
import sim.portrayal.network.SpatialNetwork2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.Valuable;
import sim.util.media.chart.TimeSeriesAttributes;
import sim.util.media.chart.TimeSeriesChartGenerator;

public class PredatorPreyModelWithUI extends GUIState {

  public static void main(String[] args) {
    PredatorPreyModelWithUI vid = new PredatorPreyModelWithUI();
    Console c = new Console(vid);
    c.setVisible(true);
  }

  public Display2D display;
  public JFrame displayFrame;
  public ContinuousPortrayal2D worldPortrayal = new ContinuousPortrayal2D();
  public NetworkPortrayal2D sheepsPortrayal = new NetworkPortrayal2D();
  public NetworkPortrayal2D wolfsPortrayal = new NetworkPortrayal2D();
  public NetworkPortrayal2D undercoverPortrayal = new NetworkPortrayal2D();
  public TimeSeriesChartGenerator ecoSeries;
  public TimeSeriesAttributes sheepSeries;
  public TimeSeriesAttributes wolfSeries;

  public PredatorPreyModelWithUI() {
    // super(new SIRModel(System.currentTimeMillis()));
    super(new PredatorPreyModel(42, new FirstReactionMethod()));
    // super(new SIRModel(50));
  }

  public PredatorPreyModelWithUI(SimState state) {
    super(state);
  }

  public static String getName() {
    return "SSA Predator Prey Model";
  }

  public Object getSimulationInspectedObject() {
    return state;
  }

  public Inspector getInspector() {
    Inspector i = super.getInspector();
    i.setVolatile(true);
    return i;
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
    PredatorPreyModel model = (PredatorPreyModel) state;

    ecoSeries.clearAllSeries();

    worldPortrayal.setField(model.world);
    worldPortrayal.setPortrayalForAll(
        new LabelledPortrayal2D(
            new OvalPortrayal2D(2.0) {
              private static final long serialVersionUID = 1L;

              public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                if (object instanceof Wolf) {
                  paint = Color.gray;
                  this.scale = Wolf.maxHunger - ((Wolf) object).hunger + 1;
                } else if (object instanceof Sheep) {
                  paint = Color.blue;
                  this.scale = 2.0;
                }
                super.draw(object, graphics, info);
              }
            },
            null));

    sheepsPortrayal.setField(new SpatialNetwork2D(model.world, model.sheep));
    sheepsPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
    wolfsPortrayal.setField(new SpatialNetwork2D(model.world, model.wolves));
    wolfsPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
    undercoverPortrayal.setField(new SpatialNetwork2D(model.world, model.undercoverNet));
    undercoverPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());

    display.reset();
    display.setBackground(Color.white);

    display.repaint();

    ChartUtilities.scheduleSeries(
        this,
        sheepSeries,
        new Valuable() {
          @Override
          public double doubleValue() {
            return model.getNumSheep();
          }
        });
    ChartUtilities.scheduleSeries(
        this,
        wolfSeries,
        new Valuable() {
          @Override
          public double doubleValue() {
            return model.getNumWolves();
          }
        });
  }

  public void init(Controller c) {
    super.init(c);
    display = new Display2D(600, 600, this);
    display.setClipping(false);

    displayFrame = display.createFrame();
    displayFrame.setTitle("Eco System Display");
    c.registerFrame(displayFrame);
    displayFrame.setVisible(true);
    display.attach(worldPortrayal, "World");
    display.attach(sheepsPortrayal, "Sheep Network");
    display.attach(wolfsPortrayal, "Wolf Network");
    display.attach(undercoverPortrayal, "Undercover Network");

    ecoSeries =
        ChartUtilities.buildTimeSeriesChartGenerator(this, "Ecosystem Populations", "number");
    sheepSeries = ChartUtilities.addSeries(ecoSeries, "sheep");
    wolfSeries = ChartUtilities.addSeries(ecoSeries, "wolf");
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
