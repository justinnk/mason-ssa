package org.justinnk.ssamason.extension.ssa;

import org.justinnk.ssamason.extension.SSASimState;

/** Interface providing common methods of stochastic simulation algorithms. */
public abstract class StochasticSimulationAlgorithm {

	/** The SSAs view of the current simulation state. */
	protected SSASimState state;

	public StochasticSimulationAlgorithm() {

	}

	/** Initialise the SSA. 
	 * @param model The initial model state.
	 */
	public void init(SSASimState model) {
		this.state = model;
	}
}
