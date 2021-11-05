/*
 * MIT License
 *
 * Copyright (c) 2021 Justin Kreikemeyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.justinnk.ssamason.extension.aspects;

import org.justinnk.ssamason.extension.Agent;
import org.justinnk.ssamason.extension.Guard;
import org.justinnk.ssamason.extension.NoAgentAttribute;
import org.justinnk.ssamason.extension.RateExpression;
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
