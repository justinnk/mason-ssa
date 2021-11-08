/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.extension;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Signal to a dependency-based SSA that the annotated field is not to be tracked as an attribute of
 * the owning agent. This is useful when using fields that are not final, but effectively constant,
 * i.e. do not change during the simulation. Such fields may be useful when they must be initialised
 * in the constructor. HANDLE WITH CARE, as this might break dependency-based SSAs.
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface NoAgentAttribute {}
