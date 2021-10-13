package org.justinnk.ssamason.extension.ssa;

import org.justinnk.ssamason.extension.Action;
import org.justinnk.ssamason.extension.Agent;
import sim.field.network.Edge;
import sim.field.network.Network;

import java.util.*;
import java.util.stream.Collectors;

/* TODO: all nodes in all networks have to be agents. */
/* TODO: framework is written such that it can easily be extended (dependencyBasedSSA,...) */
/* TODO: how to use this as a package */

/**
 * Class for storing a dependency graph between edges of networks and agents.
 * This graph can be used by dependency-based SSAs to only update the events for
 * affected actions.
 */
public class EdgeDependencyGraph {
	/**
	 * Lookup for dependents of an attribute. An entry {a,b,c} means that a edge b
	 * of network a has the list of actions c as dependents.
	 */
	private HashMap<Network, HashMap<Edge, HashSet<Action>>> dependents;

	/**
	 * Lookup for dependencies of an action. An entry {a,b,c} means that an action a
	 * depends on the list of edges c of network b.
	 */
	private HashMap<Action, HashMap<Network, HashSet<Edge>>> dependsOn;

	public EdgeDependencyGraph() {
		this.dependents = new HashMap<>();
		this.dependsOn = new HashMap<>();
	}

	/**
	 * Store the fact that an Action action depends on the attribute of a certain
	 * Agent owner.
	 * 
	 * @param action The action that is in the role of the dependent.
	 * @param edge   The attribute that the action depends on.
	 * @param owner  The owner of that attribute (used to uniquely identify the
	 *               attribute).
	 */
	public void addDependency(Action action, Edge edge, Network owner) {
		/* Add to dependents */
		if (!dependents.containsKey(owner)) {
			HashMap<Edge, HashSet<Action>> attributeDeps = new HashMap<>();
			HashSet<Action> deps = new HashSet<>();
			deps.add(action);
			attributeDeps.put(edge, deps);
			dependents.put(owner, attributeDeps);
		} else if (!dependents.get(owner).containsKey(edge)) {
			HashSet<Action> deps = new HashSet<>();
			deps.add(action);
			dependents.get(owner).put(edge, deps);
		} else { // HashSet will handle possible duplicates
			dependents.get(owner).get(edge).add(action);
		}

		/* Add to depends on */
		if (!dependsOn.containsKey(action)) {
			HashMap<Network, HashSet<Edge>> attributeDeps = new HashMap<>();
			HashSet<Edge> deps = new HashSet<>();
			deps.add(edge);
			attributeDeps.put(owner, deps);
			dependsOn.put(action, attributeDeps);
		} else if (!dependsOn.get(action).containsKey(owner)) {
			HashSet<Edge> deps = new HashSet<>();
			deps.add(edge);
			dependsOn.get(action).put(owner, deps);
		} else { // HashSet will handle possible duplicates
			dependsOn.get(action).get(owner).add(edge);
		}
	}

	public void RemoveEdge(Edge edge) {
		HashSet<Action> dependingActions = new HashSet<>();
		for (Network owner : dependents.keySet()) {
			for (Iterator<Edge> edgeIt = dependents.get(owner).keySet().iterator(); edgeIt.hasNext();) {
				Edge next = edgeIt.next();
				dependingActions.addAll(dependents.get(owner).get(next));
				if (next == edge) {
					edgeIt.remove();
				}
			}
		}
		for (Action action : dependingActions) {
			for (Network owner : dependsOn.get(action).keySet()) {
				for (Iterator<Edge> edgeIt = dependsOn.get(action).get(owner).iterator(); edgeIt.hasNext();) {
					if (edgeIt.next() == edge) {
						edgeIt.remove();
					}
				}
			}
		}
	}

	/**
	 * Remove an action from all dependency relations.
	 * 
	 * @param action The action that should be removed.
	 */
	public void RemoveDependencies(Action action) {
		/* Get the owners of the edges that this action depends on */
		HashMap<Network, HashSet<Edge>> owners = dependsOn.get(action);
		if (owners != null) {
			for (Network owner : owners.keySet()) {
				/* Get the edges this action depends on */
				HashSet<Edge> edges = dependsOn.get(action).get(owner);
				if (edges != null) {
					/* Remove the action from the edge dependency */
					for (Edge edge : edges) {
						dependents.get(owner).get(edge).remove(action);
					}
				}
			}
		}
		/* Also remove the action from dependsOn */
		dependsOn.remove(action);
	}

	/**
	 * Remove an agents actions and attributes from all dependency relations. This
	 * can be used when removing an agent from the simulation.
	 * 
	 * @param owner The agent which should be removed from all dependency relations.
	 */
	public void RemoveDependencies(Agent owner) {
		/* Remove all actions of this agent from the dependencies */
		for (Action action : owner.getActions()) {
			RemoveDependencies(action);
		}
	}

	/**
	 * Get all dependents of a certain edge owned by a specific network. This can be
	 * used to determine which actions need to be reconsidered when an edge is
	 * removed changes.
	 * 
	 * @param edge  The edge whose dependents are requested.
	 * @param owner The owner of the edge.
	 * @return All dependents of the given edge.
	 */
	public HashSet<Action> GetDependents(Edge edge, Network owner) {
		HashMap<Edge, HashSet<Action>> d = dependents.get(owner);
		if (d != null) {
			return dependents.get(owner).get(edge);
		} else {
			return new HashSet<>(0);
		}
	}

	/**
	 * Get all edges owned by the given network.
	 * 
	 * @param owner The network to query for edges.
	 * @return A set of all the owners edges known to the graph.
	 */
	public ArrayList<Action> GetDependents(Network owner) {
		HashMap<Edge, HashSet<Action>> d = dependents.get(owner);
		if (d != null) {
			return (ArrayList<Action>) d.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
		} else {
			return new ArrayList<>(0);
		}
	}

	/**
	 * Get all candidate actions, whose guard or rate might be affected by the
	 * addition of an edge.
	 * 
	 * @param newEdge The edge added. Only those actions are considered that depend
	 *                on an edge that shares a node with the new edge.
	 * @param owner   The network this edge belongs to.
	 * @return A list of all actions that potentially need to be updated.
	 */
	public ArrayList<Action> GetNewDependentCandidates(Edge newEdge, Network owner) {
		ArrayList<Action> result = new ArrayList<>();
		/* Get all actions that depend on the network */
		ArrayList<Action> networkDependents = GetDependents(owner);
		for (Action dep : networkDependents) {
			/* Get the edges this action depends on */
			HashSet<Edge> dependencies = dependsOn.get(dep).get(owner);
			for (Edge edep : dependencies) {
				/* If any of the edges shares a node with the new edge, add it to the result */
				if (edep.getFrom() == newEdge.getFrom() || edep.getTo() == newEdge.getFrom()
						|| edep.getFrom() == newEdge.getTo() || edep.getTo() == newEdge.getTo()) {
					result.add(dep);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Print all dependents of edges of networks.
	 */
	public void printAttributeDependents() {
		System.out.println("Edge Dependencies:");
		for (Network network : dependents.keySet()) {
			for (Edge edge : dependents.get(network).keySet()) {
				System.out.println(edge + " of network " + network + " has the dependents:");
				for (Action action : dependents.get(network).get(edge)) {
					System.out.println("\t" + action + ".");
				}
			}
		}
		System.out.println("End");
	}

	/**
	 * Print dependencies for each action.
	 */
	public void printActionDependencies() {
		System.out.println("Edge Dependencies:");
		for (Action action : dependsOn.keySet().stream().sorted((x, y) -> x.getOwner().getId() - y.getOwner().getId())
				.collect(Collectors.toList())) {
			System.out.println(action + " depends on:");
			for (Network network : dependsOn.get(action).keySet()) {
				for (Edge edge : dependsOn.get(action).get(network)) {
					System.out.println("\t" + edge + "of network " + network + ".");
				}
			}
		}
		System.out.println("End");
	}

	/**
	 * Remove all data from the graph.
	 */
	public void clearAllDependencies() {
		this.dependents = new HashMap<>();
		this.dependsOn = new HashMap<>();
	}
}
