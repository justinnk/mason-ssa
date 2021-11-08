/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.demo.ssamason.sirs;

import java.awt.*;
import javax.swing.*;
import org.justinnk.ssamason.extension.graphs.GridGraphCreator;
import org.justinnk.ssamason.extension.ssa.FirstReactionMethod;
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

/** GUI for the network-base SIR model. */
public class SirsModelWithUI extends GUIState {

  public static void main(String[] args) {
    SirsModelWithUI vid = new SirsModelWithUI();
    Console c = new Console(vid);
    c.setVisible(true);
  }

  public Display2D display;
  public JFrame displayFrame;
  public ContinuousPortrayal2D worldPortrayal = new ContinuousPortrayal2D();
  public NetworkPortrayal2D contactPortrayal = new NetworkPortrayal2D();
  public TimeSeriesChartGenerator SIRSeries;
  public TimeSeriesAttributes infectionSeries;
  public TimeSeriesAttributes recoverySeries;
  public TimeSeriesAttributes susceptibleSeries;

  public SirsModelWithUI() {
    // super(new SIRModel(System.currentTimeMillis()));
    // super(new SIRModel(42, new FirstReactionMethod(), new
    // ErdosRenyiGraphCreator(42, 0.1)));
    super(new SirsModel(42, new FirstReactionMethod(), new GridGraphCreator()));
    // super(new SIRModel(50));
  }

  public SirsModelWithUI(SimState state) {
    super(state);
  }

  public static String getName() {
    return "Basic SIR Model";
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
    SirsModel model = (SirsModel) state;

    SIRSeries.clearAllSeries();

    worldPortrayal.setField(model.world);
    worldPortrayal.setPortrayalForAll(
        new LabelledPortrayal2D(
            new OvalPortrayal2D(2.0) {
              private static final long serialVersionUID = 1L;

              public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                Human h = (Human) object;

                switch (h.infectionState) {
                  case SUSCEPTIBLE:
                    paint = Color.blue;
                    break;
                  case INFECTIOUS:
                    paint = Color.red;
                    break;
                  case RECOVERED:
                    paint = Color.green;
                    break;
                }
                super.draw(object, graphics, info);
              }
            },
            null));

    contactPortrayal.setField(new SpatialNetwork2D(model.world, model.contacts));
    contactPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());

    display.reset();
    display.setBackground(Color.white);

    display.repaint();

    ChartUtilities.scheduleSeries(
        this,
        infectionSeries,
        new Valuable() {
          @Override
          public double doubleValue() {
            return model.getNumInfected();
          }
        });
    ChartUtilities.scheduleSeries(
        this,
        recoverySeries,
        new Valuable() {
          @Override
          public double doubleValue() {
            return model.getNumRecovered();
          }
        });
    ChartUtilities.scheduleSeries(
        this,
        susceptibleSeries,
        new Valuable() {
          @Override
          public double doubleValue() {
            return model.getNumSusceptible();
          }
        });
  }

  public void init(Controller c) {
    super.init(c);
    display = new Display2D(600, 600, this);
    display.setClipping(false);

    displayFrame = display.createFrame();
    displayFrame.setTitle("Contact Display");
    c.registerFrame(displayFrame);
    displayFrame.setVisible(true);
    display.attach(contactPortrayal, "Contacts");
    display.attach(worldPortrayal, "World");

    SIRSeries =
        ChartUtilities.buildTimeSeriesChartGenerator(this, "Infection curves", "number of people");
    infectionSeries = ChartUtilities.addSeries(SIRSeries, "infectious");
    recoverySeries = ChartUtilities.addSeries(SIRSeries, "recovered");
    susceptibleSeries = ChartUtilities.addSeries(SIRSeries, "susceptible");
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
