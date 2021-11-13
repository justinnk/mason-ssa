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

package org.justinnk.masonssa.extension;

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
