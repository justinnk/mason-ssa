package org.justinnk.ssamason.extension;

/**
 * Functional interface for a guard expressions returning a boolean upon
 * evaluation.
 */
public interface Guard {
	/** Evaluate this condition. */
	boolean evaluate();
}
