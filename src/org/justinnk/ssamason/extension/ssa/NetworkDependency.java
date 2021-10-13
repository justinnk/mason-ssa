package org.justinnk.ssamason.extension.ssa;

import sim.field.network.Network;

public class NetworkDependency {

	private Network network;

	public Network getNetwork() {
		return network;
	}

	public NetworkDependency(Network network) {
		this.network = network;
	}
}
