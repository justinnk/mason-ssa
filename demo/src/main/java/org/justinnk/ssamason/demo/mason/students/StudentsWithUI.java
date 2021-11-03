package org.justinnk.ssamason.demo.mason.students;

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

import javax.swing.*;
import java.awt.*;

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
		yardPortrayal.setPortrayalForAll(new OvalPortrayal2D() {
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