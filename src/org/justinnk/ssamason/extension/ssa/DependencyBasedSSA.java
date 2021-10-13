package org.justinnk.ssamason.extension.ssa;

import org.justinnk.ssamason.extension.SSASimState;

/** Base class for all SSAs that use dependencies to optimise their performance. */
public class DependencyBasedSSA extends StochasticSimulationAlgorithm {

	/** The action-attribute dependency relations. */
	public AttributeDependencyGraph attributeDependencies;
	/** The action-edge dependency relations. */
	public EdgeDependencyGraph edgeDependencies;

	/** Create a new instance of this dependency-based SSA, initialising the dependency graphs. */
	public DependencyBasedSSA() {
		super();
		this.attributeDependencies = new AttributeDependencyGraph();
		this.edgeDependencies = new EdgeDependencyGraph();
	}

	@Override
	public void init(SSASimState model) {
		super.init(model);
		/* Important: clear the dependencies when the simulation is rerun. */
		attributeDependencies.clearAllDependencies();
		edgeDependencies.clearAllDependencies();
	}

}
