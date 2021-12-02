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

package org.justinnk.masonssa.extension.aspects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.justinnk.masonssa.extension.Action;
import org.justinnk.masonssa.extension.Agent;
import org.justinnk.masonssa.extension.NoAgentAttribute;
import org.justinnk.masonssa.extension.ssa.AttributeDependencyGraph;
import org.justinnk.masonssa.extension.ssa.DependencyBasedSSA;

import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;

/** Basis for aspects that accompany dependency-based SSA. */
public abstract aspect DependencySymbiont{
	/*
	 * Fields
	 */

	/*
	 * The following have to be sets, because some pointcuts may apply multiple times
	 * in the same control flow. This is because either the modeller uses multiple
	 * or redundant attribute assignments or there is no way (that I am aware of) to
	 * define a more specific pointcut.
	 */

	/**
	 * List of actions to reschedule because of their dependency on an attribute
	 * change in the current action execution.
	 */
	HashSet<Action>toReschedule=new HashSet<Action>();

	/** List of agents that were removed during the current action execution. */
	HashSet<Agent>removedAgents=new HashSet<Agent>();

	/** List of edges that were removed during the current action execution. */
	HashSet<Edge>removedEdges=new HashSet<Edge>();

	/** List of agents that were introduced during the current action execution. */
	HashSet<Agent>addedAgents=new HashSet<Agent>();

	/*
	 * These fields are part of a workaround that eliminates the need for cflow. The
	 * old pointcuts can still be found commented out below. It was found that the
	 * current implementation of cflow (or possibly also my usage) is very
	 * inefficient.
	 */
	protected static boolean inEffect=false;
	protected static boolean inCondition=false;
	protected static boolean inRate=false;
	protected static boolean inNewAgent=false;

	public static Action currentAction=null;

	/*
	 * Pointcuts
	 */

	//	/** In the control flow of applying an effect */
	//	/pointcut inEffect():
	//		cflow(execution(void Action.applyEffect()));*
	//
	//	/** In the control flow of evaluating a guard expression */
	//	pointcut inCondition():
	//		cflow(execution(boolean Action.evaluateCondition()));
	//
	//	/** In the control flow of calculating a rate expression */
	//	pointcut inRate():
	//		cflow(execution(double Action.calculateRate()));

	/** When an Agent gets instantiated. */
	pointcut NewAgent(Agent newAgent):
		initialization(Agent+.new(..)) && target(newAgent);

	/** When a new Edge is added */
	pointcut EdgeAddition(Network network, Edge edge):
		execution(public void Network.addEdge(Edge)) && args(edge) && target(network);

	/** When an Agent is removed (killed) */
	pointcut RemoveAgent(Agent agent):
		call(public void Agent+.kill())&&target(agent);

	/** When an Edge is removed */
	pointcut EdgeRemoval(Network network, Edge edge):
		execution(public Edge Network.removeEdge(Edge)) && args(edge) && target(network);

	/** When an attribute of calledOn is **accessed** by calledBy */
	pointcut AttributeAccess(Agent calledOn, Agent calledBy):
		get(public * Agent+.*) && !@annotation(NoAgentAttribute) && target(calledOn) && this(calledBy);

	/** When an attribute of calledOn is **changed** by calledBy */
	pointcut AttributeMutation(Agent calledOn, Agent calledBy):
		set(public * Agent+.*) && !@annotation(NoAgentAttribute) && target(calledOn) && this(calledBy);

	/** When a bag of edges is **accessed** by calledBy */
	pointcut EdgeAccess(sim.field.network.Network network):
		(call(public Bag Network.getEdgesOut(*))
		||call(public Bag Network.getEdgesIn(*))
		||call(public Bag Network.getEdges(*,*)))
		&& target(network);

	/* TODO: Edge getEdge(..) */

	/*
	 * Advice for the cflow-workaround
	 */

	/* inEffect */
	before(): execution(void Action.applyEffect()) {
		inEffect = true;
	}
	after(): execution(void Action.applyEffect()) {
		inEffect = false;
	}

	/* inCondition */
	before(): execution(boolean Action.evaluateCondition()) {
		inCondition = true;
	}
	after(): execution(boolean Action.evaluateCondition()) {
		inCondition = false;
	}

	/* inRate */
	before(): execution(double Action.calculateRate()) {
		inRate = true;
	}
	after():execution(double Action.calculateRate()) {
		inRate = false;
	}

	/* inNewAgent */
	before():NewAgent(*) {
		inNewAgent = true;
	}
	after()returning:NewAgent(*) {
		inNewAgent = false;
	}

	/*
	 * Methods to handle the different scenarios of the SSA
	 */

	protected void handleAttributeAccess(DependencyBasedSSA ssa, Action currentAction, Agent calledOn,
			JoinPoint.StaticPart jp) {
		ssa.attributeDependencies.addDependency(currentAction, jp.getSignature().toShortString(), calledOn);
		// System.out.println("Attribute " + jp.getSignature().toShortString() + "
		// accessed by " + currentAction);
	}

	protected void handleEdgeAccess(DependencyBasedSSA ssa, Action currentAction, Network network, Bag edges) {
		if (edges != null && !edges.isEmpty()) {
			for (int i = 0; i < edges.size(); i++) {
				ssa.edgeDependencies.addDependency(currentAction, (Edge) edges.get(i), network);
			}
		}
		// System.out.println("Action " + currentAction + " accessed " + edges);
	}

	protected void handleAttributeMutation(DependencyBasedSSA ssa, Action currentAction, Agent calledOn, Agent calledBy,
			JoinPoint.StaticPart jp) {
		AttributeDependencyGraph deps = ssa.attributeDependencies;
		String attribute = jp.getSignature().toShortString();
		HashSet<Action> dependents = deps.GetDependents(attribute, calledOn);
		if (dependents != null) {
			toReschedule.addAll(dependents);
		}
		// System.out.println("Attribute " + attribute + " of " + calledOn + " was
		// mutated by " + calledBy);
	}

	protected void handleAgentRemoval(DependencyBasedSSA ssa, Agent agent) {
		AttributeDependencyGraph deps = ssa.attributeDependencies;
		Set<String> attributes = deps.GetAttributes(agent);
		if (attributes != null) {
			for (String attr : attributes) {
				HashSet<Action> dependents = deps.GetDependents(attr, agent);
				if (dependents != null) {
					toReschedule.addAll(dependents);
				}
			}
		}
		removedAgents.add(agent);
		// System.out.println(agent + " was killed.");
	}

	protected void handleEdgeRemoval(DependencyBasedSSA ssa, Edge edge, Network network) {
		HashSet<Action> dependents = ssa.edgeDependencies.GetDependents(edge, network);
		if (dependents != null) {
			toReschedule.addAll(dependents);
		}
		removedEdges.add(edge);
		// System.out.println(edge + " was removed.");
	}

	protected void handleAgentAddition(Agent newAgent) {
		/*
		 * Dependency updates are handled by edge addition (if this agent has no edges,
		 * it has no dependencies)
		 */
		addedAgents.add(newAgent);
		// System.out.println(newAgent + " was added.");
	}

	protected void handleEdgeAddition(DependencyBasedSSA ssa, Edge edge, Network network) {
		ArrayList<Action> candidates = ssa.edgeDependencies.GetNewDependentCandidates(edge, network);
		toReschedule.addAll(candidates);
		toReschedule.addAll(((Agent) edge.getFrom()).getActions());
		toReschedule.addAll(((Agent) edge.getTo()).getActions());
		// System.out.println(edge + " was added. This affects:");
		// System.out.println(candidates);
	}
}
