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

import org.justinnk.masonssa.extension.Agent;
import org.justinnk.masonssa.extension.Guard;
import org.justinnk.masonssa.extension.NoAgentAttribute;
import org.justinnk.masonssa.extension.RateExpression;
import sim.field.network.Network;
import sim.field.network.Edge;

import sim.engine.Schedule;

/**
 * Aspect that prohibits (some) code that would break the SSA by using syntax
 * errors. This aspect operates entirely on a syntactic level.
 */
public aspect ErrorSymbiont {

	pointcut inCondition():
		within(Agent+) && (withincode(boolean *..lambda*(..)) || withincode(public boolean Guard.evaluate()));

	pointcut inRate():
		within(Agent+) && (withincode(double *..lambda*(..)) || withincode(public double RateExpression.rate()));

	pointcut attributeMutation():
		set(public * Agent+.*) && !@annotation(NoAgentAttribute);

	pointcut timeAccess():
		call(public double Schedule.getTime()) || call(public java.lang.String Schedule.getTimestamp(*));

//	declare error: inCondition() && attributeMutation():
//		"It is not allowed to mutate an attribute in the condition of an action.\n"
//		+ "Note: because of technical reasons, this is prohibited for all lambdas returning booleans in Agent-subclasses.";
//
//	declare error: inRate() && attributeMutation():
//		"It is not allowed to mutate an attribute in the rate of an action.\n"
//		+ "Note: because of technical reasons, this is prohibited for all lambdas returning doubles in Agent-subclasses.";
//
//	declare error: inRate() && timeAccess():
//		"Time-dependent rate functions are not allowed, since time-inhomogeneous CTMC are not yet supported."
//		+ "Note: because of technical reasons, this is prohibited for all lambdas returning doubles in Agent-subclasses.";

	declare error: (inRate() || inCondition()) && call(public Edge Network.getEdge(*,*)):
		"This method is not yet supported";

	/* TODO: forbid further calls to schedule in actions... */

	/* TODO: forbid calls to Network.getAllNodes() */
}
