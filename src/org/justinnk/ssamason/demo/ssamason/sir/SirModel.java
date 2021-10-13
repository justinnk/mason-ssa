package org.justinnk.ssamason.demo.ssamason.sir;

import org.justinnk.ssamason.demo.InfectionState;
import org.justinnk.ssamason.extension.SSASimState;
import org.justinnk.ssamason.extension.graphs.ErdosRenyiGraphCreator;
import org.justinnk.ssamason.extension.graphs.GraphCreator;
import org.justinnk.ssamason.extension.ssa.FirstReactionMethod;
import org.justinnk.ssamason.extension.ssa.StochasticSimulationAlgorithm;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.Interval;

/* TODO: at the end add serialisability to extension. */

/**
 * Basic network-based susceptible-infectious-recovered model using the
 * SSA-extension to MASON.
 *
 */
public class SirModel extends SSASimState {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		doLoop(SirModel.class, args);
		System.exit(0);
	}

	public Continuous2D world = new Continuous2D(1.0, 100.0, 100.0);
	public Network contacts = new Network(false);
	public GraphCreator graph;

	private int numHumans = 100;

	public int getNumHumans() {
		return numHumans;
	}

	public void setNumHumans(int value) {
		numHumans = value;
	}

	private int initialInfected = 1;

	public int getInitialInfected() {
		return initialInfected;
	}

	public void setInitialInfected(int value) {
		initialInfected = value;
	}

	public Object domInitialInfected() {
		return new Interval(0, numHumans);
	}

	private double recoveryTime = 25.0;

	public double getRecoveryRate() {
		return 1.0 / recoveryTime;
	}

	private double infectionTime = 10.0;

	public double getInfectionRate() {
		return 1.0 / infectionTime;
	}

	public int getNumSusceptible() {
		Bag humans = contacts.getAllNodes();
		int num = 0;
		for (int i = 0; i < humans.size(); i++) {
			if (((Human) humans.get(i)).infectionState == InfectionState.SUSCEPTIBLE) {
				num += 1;
			}
		}
		return num;
	}

	public int getNumInfected() {
		Bag humans = contacts.getAllNodes();
		int num = 0;
		for (int i = 0; i < humans.size(); i++) {
			if (((Human) humans.get(i)).infectionState == InfectionState.INFECTIOUS) {
				num += 1;
			}
		}
		return num;
	}

	public int getNumRecovered() {
		Bag humans = contacts.getAllNodes();
		int num = 0;
		for (int i = 0; i < humans.size(); i++) {
			if (((Human) humans.get(i)).infectionState == InfectionState.RECOVERED) {
				num += 1;
			}
		}
		return num;
	}

	public SirModel(long seed) {
		super(seed, new FirstReactionMethod());
		this.graph = new ErdosRenyiGraphCreator(42, 0.2);
	}

	public SirModel(long seed, StochasticSimulationAlgorithm simulator, GraphCreator graph) {
		super(seed, simulator);
		this.graph = graph;
	}

	public void start() {
		super.start();
		world.clear();
		contacts.clear();
		// MersenneTwisterFast lrandom = new MersenneTwisterFast(42);
		for (int i = 0; i < numHumans; i++) {
			Human h = new Human(this);
			/* Set initial infection */
			if (i < initialInfected) {
				h.infectionState = InfectionState.INFECTIOUS;
			}
			/* Place node at random location */
			/*
			 * world.setObjectLocation(h, new Double2D( world.getWidth() * 0.5 +
			 * (lrandom.nextDouble() - 0.5) * 100.0, world.getHeight() * 0.5 +
			 * (lrandom.nextDouble() - 0.5) * 100.0));
			 */
			int rows = (int) Math.sqrt(numHumans);
			world.setObjectLocation(h, new Double2D(world.getWidth() * 0.1 + (int) (i / rows) * 8.0,
					world.getHeight() * 0.1 + (i % rows) * 8.0));
			contacts.addNode(h);
		}
		graph.create(contacts);
	}

	@Override
	public void finish() {
		super.finish();
	}
}
