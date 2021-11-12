/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.extension.ssa;

import java.util.ArrayList;
import org.justinnk.masonssa.extension.Action;
import org.justinnk.masonssa.extension.SSASimState;
import sim.engine.SimState;
import sim.engine.Steppable;

/** Class implementing an aspect-oriented direct method (AO-DM). */
public class OptimizedDirectMethod extends DependencyBasedSSA {

  /** The nextAction. This needs to be global, so it can be used in the in line Steppable class. */
  public Action nextAction;
  /** The cache for the sum of rates needed for the second loop in the DM step. */
  private double rateSum;

  public OptimizedDirectMethod() {
    super();
    /* Probably not needed, but safe is safe. */
    this.rateSum = 0;
  }

  @Override
  public void init(SSASimState model) {
    super.init(model);
    this.rateSum = 0;
    nextAction = null;
    /* Use LogarithmicDirectMethod step for initialisation */
    ArrayList<Double> partialSums = new ArrayList<>(Action.ActionInstances.size());
    ArrayList<Action> applicableActions = new ArrayList<>(Action.ActionInstances.size());
    /* calculate sum of propensities and list of partial sums */
    for (Action action : Action.ActionInstances) {
      double rate = initAction(action);
      if (rate > 0) { // implicit: rate != Double.NEGATIVE_INFINITY
        this.rateSum += rate;
        partialSums.add(this.rateSum);
        applicableActions.add(action);
      }
    }
    if (applicableActions.size() > 0) {
      /* draw two random numbers */
      double r1 = state.random.nextDouble();
      double r2 = state.random.nextDouble();
      /* calculate waiting time */
      double waitingTime = (1.0 / this.rateSum) * Math.log(1 / r1);
      /* perform binary search for next action */
      double propensityLimit = r2 * this.rateSum;
      int rightBound = partialSums.size() - 1;
      int leftBound = 0;
      int center;
      while (leftBound <= rightBound) {
        center = leftBound + ((rightBound - leftBound) / 2);
        if ((center == 0 && partialSums.get(center) > propensityLimit)
            || (center > 0
                && partialSums.get(center) > propensityLimit
                && partialSums.get(center - 1) <= propensityLimit)) {
          nextAction = applicableActions.get(center);
          break;
        } else if (partialSums.get(center) > propensityLimit) {
          rightBound = center - 1;
        } else {
          leftBound = center + 1;
        }
      }

      if (nextAction != null) {
        state.schedule.scheduleOnce(
            waitingTime,
            new Steppable() {
              private static final long serialVersionUID = 1L;

              @Override
              public void step(SimState state) {
                executeAction(nextAction);
                OptimizedDirectMethod.this.step();
              }
            });
      }
    }
    //		attributeDependencies.printActionDependencies();
    //		edgeDependencies.printActionDependencies();
  }

  /**
   * Initialise an action by calculating its rate and adding it to the sum of rates (if applicable).
   *
   * @param action The action to initialise.
   */
  public double initAction(Action action) {
    double rate;
    if (action.evaluateCondition()) {
      rate = action.calculateRate();
      return rate;
    }
    return Double.NEGATIVE_INFINITY;
  }

  public void step() {
    /* draw two random numbers */
    double r1 = state.random.nextDouble();
    double r2 = state.random.nextDouble();
    nextAction = null;
    /*
     * Fix floating point precision error resulting from constant addition and
     * subtraction.
     */
    if (this.rateSum < 0.0) {
      this.recalculateRateSum();
    }
    /* calculate waiting time */
    double waitingTime = (1.0 / this.rateSum) * Math.log(1 / r1);
    double propensityLimit = r2 * this.rateSum;
    /* Determine the next action based on the sum of rates. */
    double partialSum = 0;
    for (Action action : Action.ActionInstances) {
      if (action.getCurrentGuard()) {
        double rate = action.getCurrentRate();
        partialSum += rate;
        if (partialSum > propensityLimit) {
          nextAction = action;
          break;
        }
      }
    }
    /*
     * If nextAction is null, we possibly had a floating point error and the limit
     * was too high. Thus we need to double-check.
     */
    if (nextAction == null) {
      this.recalculateRateSum();
      waitingTime = (1.0 / this.rateSum) * Math.log(1 / r1);
      propensityLimit = r2 * this.rateSum;
      partialSum = 0;
      for (Action action : Action.ActionInstances) {
        if (action.getCurrentGuard()) {
          double rate = action.getCurrentRate();
          partialSum += rate;
          if (partialSum > propensityLimit) {
            nextAction = action;
            break;
          }
        }
      }
    }
    /* Now we can schedule the next event. */
    if (waitingTime != Double.POSITIVE_INFINITY && nextAction != null) {
      state.schedule.scheduleOnceIn(
          waitingTime,
          new Steppable() {
            private static final long serialVersionUID = 1L;

            @Override
            public void step(SimState state) {
              executeAction(nextAction);
              OptimizedDirectMethod.this.step();
            }
          });
    }
  }

  /**
   * Apply an actions effect (if it is not null).
   *
   * @param action The action, whose effect is applied.
   */
  public void executeAction(Action action) {
    if (action != null) {
      action.applyEffect();
    }
  }

  /**
   * Update the sum of rates with the actions new rate.
   *
   * @param action The action to account for.
   */
  public void updateRateSum(Action action) {
    /* If the actions old rate is in the sum, subtract it. */
    removeFromRateSum(action);
    /* In any case, add the new rate of the action, if applicable. */
    if (action.evaluateCondition()) {
      double rate = action.calculateRate();
      this.rateSum += rate;
    }
  }

  /**
   * Remove the actions old rate from the sum. This is useful if the agent owning that action was
   * killed.
   *
   * @param action The action to account for
   */
  public void removeFromRateSum(Action action) {
    if (action.getCurrentGuard()) {
      double oldRate = action.getCurrentRate();
      this.rateSum -= oldRate;
    }
  }

  /** Recalculate the sum of rates. This can be used to correct numerical errors. */
  public void recalculateRateSum() {
    this.rateSum = 0;
    for (Action action : Action.ActionInstances) {
      if (action.evaluateCondition()) {
        double rate = action.calculateRate();
        if (rate > 0) {
          // System.out.println("adding " + action + "s " + rate);
          this.rateSum += rate;
        }
      }
    }
  }
}
