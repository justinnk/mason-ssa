/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.extension;

/** Functional interface for a rate expression that can be calculated. */
public interface RateExpression {
  /** Calculate the rate. */
  double rate();
}
