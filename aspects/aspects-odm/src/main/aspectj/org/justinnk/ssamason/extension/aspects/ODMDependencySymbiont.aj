/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.extension.aspects;

import org.justinnk.ssamason.extension.Action;
import org.justinnk.ssamason.extension.SSASimState;
import org.justinnk.ssamason.extension.Agent;
import org.justinnk.ssamason.extension.ssa.OptimizedDirectMethod;

import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;

/** Aspect that informs the ODM SSA about the dependencies. Separates the concern of optimisation from the rest of the code.
 *
 * 1. Initial Dependency Graph (Evaluation of every guard and rate)
 * 		- add a dependency from an action on every **attribute** that is accessed in rate() or guard()
 * 		- add a dependency from an action on every **edge** that is accessed  in rate() or guard()
 * 2. Step (Execution of the imminent event)
 * 		- In a step, several things can happen, that use or alter the dependencies
 * 		2.1 Attribute Mutation
 * 			- store all dependents of that attribute in toReschedule
 * 		2.2 Agent Removal (stored in `killedAgents`)
 * 			- store all dependents of that agents attributes in attributeReschedule
 * 			- store agent in removedAgents
 * 		2.3 Edge Removal
 * 			- store all dependents of that edge in toReschedule
 * 			- store edge in removedEdges
 * 		2.4 Agent Addition
 * 			- store agent in bornAgents
 * 		2.5 Edge Addition
 * 			- Find all edges in the dependencies that share a node with the new edge
 * 			- store all actions that depend on those edges in toReschedule
 * 			- store all actions of the nodes that participate in the edge in toReschedule
 * 		(2.6 Edge Label Mutation)
 * 		(2.7 List Attribute Mutation)
 * 3. After a step
 * 		- for all agents in removedAgents, stop their actions and remove them from the dependency graphs
 *      - for all edges in removedEdges, remove them from the dependency graphs
 *      - for all actions in toReschedule, update the rate sum accordingly and reinitialise their dependencies
 *      - for all agents in bornAgents, initialise them and their dependencies
 *
 */
public aspect ODMDependencySymbiont extends DependencySymbiont {

	private static boolean inStep = false;
	private static boolean inReschedule = false;
	private static boolean inInit = false;
	private static boolean inInitAction = false;
	private OptimizedDirectMethod odm;

	/*
	 * Pointcuts
	 */

	/* Step of the odm */
	pointcut Step(OptimizedDirectMethod odm, Action currentAction):
			call(public void OptimizedDirectMethod.executeAction(..)) && args(currentAction) && target(odm);

	/* In the control flow of the initialisation of the optimized direct method */
	/*pointcut inInit(OptimizedDirectMethod odm):
			cflow(execution(public void OptimizedDirectMethod.init(SSASimState)) && target(odm));*/

	/* In the control flow of the initialisation of an Action */
	/*pointcut inInitAction(Action currentAction):
			cflow(execution(public double OptimizedDirectMethod.initAction(..)) && args(currentAction));*/

	/*pointcut inInitAction2(OptimizedDirectMethod odm, Action currentAction):
		cflow(execution(public double OptimizedDirectMethod.initAction(..)) && target(odm) && args(currentAction));*/

	/* In the control flow of the step of the next reaction method */
	/*pointcut inStep(OptimizedDirectMethod odm, Action currentAction):
			cflow(execution(void OptimizedDirectMethod.executeAction(..)) && args(currentAction) && target(odm));*/

	/* In the control flow of the rescheduling of an action in the optimized direct method*/
	/*pointcut inReschedule(OptimizedDirectMethod odm, Action action, Action trigger):
			cflow(execution(void OptimizedDirectMethod.updateRateSum(..)) && args(action, trigger) && target(odm));*/

	/* inStep */
	before(OptimizedDirectMethod odm, Action currentAction):
		execution(void OptimizedDirectMethod.executeAction(..)) && args(currentAction) && target(odm)
	{
		inStep = true;
		this.odm = odm;
		ODMDependencySymbiont.currentAction = currentAction;
	}
	after() returning: execution(void OptimizedDirectMethod.executeAction(..))
	{
		inStep = false;
	}

	/* inReschedule */
	before(OptimizedDirectMethod odm, Action currentAction):
		execution(void OptimizedDirectMethod.updateRateSum(..)) && args(currentAction) && target(odm)
	{
		inReschedule = true;
		this.odm = odm;
		ODMDependencySymbiont.currentAction = currentAction;
	}
	after() returning: execution(void OptimizedDirectMethod.updateRateSum(..))
	{
		inReschedule = false;
	}

	/* inInitAction */
	before(OptimizedDirectMethod odm, Action currentAction):
		execution(public double OptimizedDirectMethod.initAction(..)) && target(odm) && args(currentAction)
	{
		inInitAction = true;
		this.odm = odm;
		ODMDependencySymbiont.currentAction = currentAction;
	}
	after() returning: execution(public double OptimizedDirectMethod.initAction(..))
	{
		inInitAction = false;
	}

	/* inInit */
	before(OptimizedDirectMethod odm):
		execution(public void OptimizedDirectMethod.init(SSASimState)) && target(odm)
	{
		inInit = true;
		this.odm = odm;
	}
	after() returning: execution(public void OptimizedDirectMethod.init(SSASimState))
	{
		inInit = false;
	}

	/*
	 * Advices
	 */

	/*
	 * 1. Initial Dependency Graph
	 */

	/* After an attribute of calledOn is accessed by calledBy in the control flow of the next reaction methods initialisation */
	/* all getters in NRM -> init -> initAction -> Action.evaluateCondition() / Action.calculateRate() */
	after (Agent calledOn, Agent calledBy):
		AttributeAccess(calledOn, calledBy) && if(inInit && inInitAction) && if(inCondition || inRate)
	{
		/* That a getter is called during initialisation of the NRM means that the currently initialised action depends on the respective attribute.
		 * Add this fact to the dependency graph.
		 */
		handleAttributeAccess(odm, currentAction, calledOn, thisJoinPointStaticPart);
	}

	/* Access to edge */
	after(Network network) returning(Bag edges):
		EdgeAccess(network) && if(inInit && inInitAction) && if(inRate || inCondition)
	{
		handleEdgeAccess(odm, currentAction, network, edges);
	}

	/*
	 * 2. Step (Execution of the imminent event)
	 */

	/* 2.1 Attribute Mutation */
	/* After an attribute of calledOn was mutated by calledBy in a step */
	/* all setters in NRM -> step -> applyEffect() and not in an new Agent() call */
	after(Agent calledOn, Agent calledBy) returning:
		AttributeMutation(calledOn, calledBy) && if(inStep && inEffect && !inNewAgent)
	{
		/* That a setter is called during the execution of an action means that an attribute changed.
		 * Thus we have to retrieve all actions that depend on this attribute and possibly reschedule them.
		 */
		handleAttributeMutation(this.odm, currentAction, calledOn, calledBy, thisJoinPointStaticPart);
	}

	/* 2.2 Agent Removal */
	/* After the kill method has been called on an agent in the control flow of an effect in an odm step */
	/* Agent.kill() in NRM -> step -> applyEffect() */
	after(Agent agent):
		RemoveAgent(agent) && if(inStep && inEffect)
	{
		handleAgentRemoval(odm, agent);
	}

	/* 2.3 Edge Removal */
	/* Removal of an edge */
	after(Edge edge, Network network):
		EdgeRemoval(network, edge) && if(inStep && inEffect)
	{
		handleEdgeRemoval(odm, edge, network);
	}

	/* 2.4 Agent Addition */
	/* After a new agent is instantiated in an actions effect */
	/* new Agent() in NRM -> step -> Action.applyEffect() */
	after(Agent newAgent):
		NewAgent(newAgent) && if(inStep && inEffect)
	{
		handleAgentAddition(newAgent);
	}
	/* When a new agent is initialised. */
	/* all getters in NRM -> initAction -> Action.evaluateCondition() / Action.calculateRate(), but not in init */
	after(Agent calledOn, Agent calledBy):
		AttributeAccess(calledOn, calledBy) && if(inInitAction && !inInit) && if(inCondition || inRate)
	{
		handleAttributeAccess(odm, currentAction, calledOn, thisJoinPointStaticPart);
	}
	after(Network network) returning(Bag edges):
		EdgeAccess(network) && if(inInitAction && !inInit) && if(inCondition || inRate)
	{
		handleEdgeAccess(this.odm, currentAction, network, edges);
	}

	/* 2.5 Edge Addition */
	/* Addition of an edge */
	after(Edge edge, Network network):
		EdgeAddition(network, edge) && if(inStep && inEffect)
	{
		handleEdgeAddition(this.odm, edge, network);
	}

	/*
	 * 3. After Step
	 */

	/* After an attribute was accessed in reschedule */
	after(Agent calledOn, Agent calledBy):
		AttributeAccess(calledOn, calledBy) && if(inReschedule)
	{
		handleAttributeAccess(this.odm, currentAction, calledOn, thisJoinPointStaticPart);
	}
	/* After an edge was accessed in reschedule */
	after(Network network) returning(Bag edges):
		EdgeAccess(network) && if(inReschedule)
	{
		handleEdgeAccess(this.odm, currentAction, network, edges);
	}

	/*
	 * Conclusion of a Step
	 */

	/* After a step of the NRM concluded. */
	after(OptimizedDirectMethod odm, Action currentAction) returning:
		Step(odm, currentAction)
	{
		toReschedule.add(currentAction);
		/* 2.2 */
		/* Loop through all killed agents and remove their dependencies/dependents. */
		for (Agent agent : removedAgents)
		{
			odm.attributeDependencies.RemoveDependencies(agent);
			odm.edgeDependencies.RemoveDependencies(agent);
			for (Action a : agent.getActions())
			{
				odm.removeFromRateSum(a);
			}
		}
		/* 2.3 */
		for (Edge edge : removedEdges)
		{
			odm.edgeDependencies.RemoveEdge(edge);
		}
		/* 2.1, 2.2, 2.5, 2.3 */
		/* Loop through all attribute dependents and reschedule their events. */
		for (Action dep : toReschedule)
		{
			/* If the owner of this action was removed, do not reschedule it. */
			if (!removedAgents.contains(dep.getOwner()))
			{
				odm.attributeDependencies.RemoveDependencies(dep);
				odm.edgeDependencies.RemoveDependencies(dep);
				odm.updateRateSum(dep);
			}
		}
		/* 2.4 */
		/* Loop through all new agents and initialise them. */
		for (Agent agent : addedAgents)
		{
			/* If this new agent was also removed in the same step, do not initialise. */
			if (!removedAgents.contains(agent))
			{
				for (Action action : agent.getActions())
				{
					odm.updateRateSum(action);
				}
			}
		}
		removedAgents.clear();
		removedEdges.clear();
		toReschedule.clear();
		addedAgents.clear();

		/* Debug */
//		System.out.println("---------------------------");
//		System.out.println("step finished. New State: ");
//		odm.attributeDependencies.printAttributeDependents();
//		odm.edgeDependencies.printAttributeDependents();
//		System.out.println("The new current action is:");
//		System.out.println(odm.nextAction);
//		System.out.println("---------------------------");
	}
}
