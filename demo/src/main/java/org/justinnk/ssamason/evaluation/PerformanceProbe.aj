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

package org.justinnk.ssamason.evaluation;

import org.justinnk.ssamason.evaluation.benchmarks.BenchmarkSheepNrm;
import org.justinnk.ssamason.evaluation.benchmarks.BenchmarkSirsErdosRenyiNrm;
import org.justinnk.ssamason.evaluation.benchmarks.BenchmarkSirsErdosRenyiVanilla;
import org.justinnk.ssamason.evaluation.benchmarks.BenchmarkSirsGridNrm;
import org.justinnk.ssamason.evaluation.benchmarks.BenchmarkSirsGridVanilla;
import org.justinnk.ssamason.extension.Action;
import org.justinnk.ssamason.extension.Effect;
import org.justinnk.ssamason.extension.ssa.StochasticSimulationAlgorithm;

import ec.util.MersenneTwisterFast;
import sim.engine.Steppable;
import sim.engine.TentativeStep;

/**
 * Aspect that counts the number of calls to certain methods
 * which are of interest to the performance evaluation, such
 * as the drawing of a random number.
 */
public aspect PerformanceProbe {

	before(TentativeStep step):
		call(public void Steppable.step(..)) && target(step)
		//&& within(sim.engine.TentativeStep)
	{
		if (step.getSteppable() == null)
		{
			BenchmarkSirsGridNrm.wasStopped = true;
			BenchmarkSirsErdosRenyiNrm.wasStopped = true;
			BenchmarkSirsGridVanilla.wasStopped = true;
			BenchmarkSirsErdosRenyiVanilla.wasStopped = true;
			BenchmarkSheepNrm.wasStopped = true;
			//System.out.println("X");
		}
		else
		{
			BenchmarkSirsGridNrm.wasStopped = false;
			BenchmarkSirsErdosRenyiNrm.wasStopped = false;
			BenchmarkSirsGridVanilla.wasStopped = false;
			BenchmarkSirsErdosRenyiVanilla.wasStopped = false;
			BenchmarkSheepNrm.wasStopped = false;
			//System.out.println("O");
		}
	}

//	after(): call(public double Action.calculateRate())
//		|| call(private void org.justinnk.ssamason.demo.mason.vanillasirs.Human.scheduleInfection())
//		|| call(private void org.justinnk.ssamason.demo.mason.vanillasirs.Human.scheduleImmunityLoss())
//		|| call(private void org.justinnk.ssamason.demo.mason.vanillasirs.Human.scheduleRecovery())
//	{
//		PerformanceMetrics.incStaticMetric("rateCalculations");
//	}
//
//	after(): call(public boolean Action.evaluateCondition()) {
//		PerformanceMetrics.incStaticMetric("conditionEvaluations");
//	}
//
//	/*after():
//		call(public void Steppable.stop())
//	{
//		PerformanceMetrics.incStaticMetric("tentativeStops");
//	}*/
//
//	after(): call(public double MersenneTwisterFast.nextDouble())
//		&& (within(StochasticSimulationAlgorithm+)
//				|| within(org.justinnk.ssamason.demo.mason.vanillasirs.Human)) {
//		PerformanceMetrics.incStaticMetric("randomNumbers");
//	}
//
//	after(): call(public void Effect.apply())
//		|| call(public void org.justinnk.ssamason.demo.mason.vanillasirs.Human.recover())
//		|| call(public void org.justinnk.ssamason.demo.mason.vanillasirs.Human.getInfected()) {
//		PerformanceMetrics.incStaticMetric("actionApplications");
//	}
}
