package org.justinnk.ssamason.demo.mason.vanillasirs;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.TentativeStep;

import java.util.HashMap;

public class Agent {

	protected SimState model;

	private HashMap<String, Steppable> actions = new HashMap<>();

	protected void addAction(String name, Steppable action) {
		actions.put(name, action);
	}

	protected TentativeStep currentAction;

	protected void scheduleActionOnceIn(double delta, String actionName) {
		currentAction = new TentativeStep(actions.get(actionName));
		model.schedule.scheduleOnceIn(delta, currentAction);
	}

	private int id;

	public int getId() {
		return id;
	}

	public Agent(SimState model, int id) {
		this.model = model;
		this.id = id;
	}

	protected void log(String message) {
		System.out.println(id + ": " + message);
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}
}
