/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.evaluation;

import org.justinnk.masonssa.evaluation.benchmarks.BenchmarkSheepNrm;
import org.justinnk.masonssa.evaluation.benchmarks.BenchmarkSirsErdosRenyiNrm;
import org.justinnk.masonssa.evaluation.benchmarks.BenchmarkSirsErdosRenyiVanilla;
import org.justinnk.masonssa.evaluation.benchmarks.BenchmarkSirsGridNrm;
import org.justinnk.masonssa.evaluation.benchmarks.BenchmarkSirsGridVanilla;
import org.justinnk.masonssa.extension.Action;
import org.justinnk.masonssa.extension.Effect;
import org.justinnk.masonssa.extension.ssa.StochasticSimulationAlgorithm;

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
//		|| call(private void org.justinnk.masonssa.demo.mason.vanillasirs.Human.scheduleInfection())
//		|| call(private void org.justinnk.masonssa.demo.mason.vanillasirs.Human.scheduleImmunityLoss())
//		|| call(private void org.justinnk.masonssa.demo.mason.vanillasirs.Human.scheduleRecovery())
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
//				|| within(org.justinnk.masonssa.demo.mason.vanillasirs.Human)) {
//		PerformanceMetrics.incStaticMetric("randomNumbers");
//	}
//
//	after(): call(public void Effect.apply())
//		|| call(public void org.justinnk.masonssa.demo.mason.vanillasirs.Human.recover())
//		|| call(public void org.justinnk.masonssa.demo.mason.vanillasirs.Human.getInfected()) {
//		PerformanceMetrics.incStaticMetric("actionApplications");
//	}
}
