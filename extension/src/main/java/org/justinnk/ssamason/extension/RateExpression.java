package org.justinnk.ssamason.extension;

/** Functional interface for a rate expression that can be calculated. */
public interface RateExpression {
	/** Calculate the rate. */
	double rate();
}
