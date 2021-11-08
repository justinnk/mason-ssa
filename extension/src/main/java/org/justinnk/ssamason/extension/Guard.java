/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.extension;

/** Functional interface for a guard expressions returning a boolean upon evaluation. */
public interface Guard {
  /** Evaluate this condition. */
  boolean evaluate();
}
