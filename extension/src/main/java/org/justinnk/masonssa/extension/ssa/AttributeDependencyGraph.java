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

package org.justinnk.masonssa.extension.ssa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.justinnk.masonssa.extension.Action;
import org.justinnk.masonssa.extension.Agent;

/**
 * Class for storing a dependency graph between attributes and agents. This graph can be used by
 * dependency-based SSAs to only update the events for affected actions.
 */
public class AttributeDependencyGraph {

  /**
   * Lookup for dependents of an attribute. An entry {a,b,c} means that an attribute b of agent a
   * has the list of actions c as dependents.
   */
  private HashMap<Agent, HashMap<String, HashSet<Action>>> dependents;

  /**
   * Lookup for dependencies of an action. An entry {a,b,c} means that an action a depends on the
   * list of attributes c of agent b.
   */
  private HashMap<Action, HashMap<Agent, HashSet<String>>> dependsOn;

  public AttributeDependencyGraph() {
    this.dependents = new HashMap<>();
    this.dependsOn = new HashMap<>();
  }

  /**
   * Store the fact that an Action action depends on the attribute of a certain Agent owner.
   *
   * @param action The action that is in the role of the dependent.
   * @param attribute The attribute that the action depends on.
   * @param owner The owner of that attribute (used to uniquely identify the attribute).
   */
  public void addDependency(Action action, String attribute, Agent owner) {
    /* Add to dependents */
    if (!dependents.containsKey(owner)) {
      HashMap<String, HashSet<Action>> attributeDeps = new HashMap<>();
      HashSet<Action> deps = new HashSet<>();
      deps.add(action);
      attributeDeps.put(attribute, deps);
      dependents.put(owner, attributeDeps);
    } else if (!dependents.get(owner).containsKey(attribute)) {
      HashSet<Action> deps = new HashSet<>();
      deps.add(action);
      dependents.get(owner).put(attribute, deps);
    } else { // HashSet will handle possible duplicates
      dependents.get(owner).get(attribute).add(action);
    }

    /* Add to depends on */
    if (!dependsOn.containsKey(action)) {
      HashMap<Agent, HashSet<String>> attributeDeps = new HashMap<>();
      HashSet<String> deps = new HashSet<>();
      deps.add(attribute);
      attributeDeps.put(owner, deps);
      dependsOn.put(action, attributeDeps);
    } else if (!dependsOn.get(action).containsKey(owner)) {
      HashSet<String> deps = new HashSet<>();
      deps.add(attribute);
      dependsOn.get(action).put(owner, deps);
    } else { // HashSet will handle possible duplicates
      dependsOn.get(action).get(owner).add(attribute);
    }
  }

  /**
   * Remove an action from all dependency relations.
   *
   * @param action The action that should be removed.
   */
  public void RemoveDependencies(Action action) {
    /* Get the owners of the attributes that this action depends on */
    HashMap<Agent, HashSet<String>> attributeOwners = dependsOn.get(action);
    if (attributeOwners != null) {
      for (Agent agent : attributeOwners.keySet()) {
        /* Get the attributes this action depends on */
        HashSet<String> attributes = dependsOn.get(action).get(agent);
        if (attributes != null) {
          /* Remove the action from the attribute dependency */
          for (String attribute : attributes) {
            dependents.get(agent).get(attribute).remove(action);
          }
        }
      }
    }
    /* Also remove the action from dependsOn */
    dependsOn.remove(action);
  }

  /**
   * Remove an agents actions and attributes from all dependency relations. This can be used when
   * removing an agent from the simulation.
   *
   * @param owner The agent which should be removed from all dependency relations.
   */
  public void RemoveDependencies(Agent owner) {
    /* First, remove all actions of this agent from the dependencies */
    for (Action action : owner.getActions()) {
      RemoveDependencies(action);
    }
    /* Next, remove all attributes of that agent from the dependencies */
    for (Action act : dependsOn.keySet()) {
      if (dependsOn.get(act).get(owner) != null) {
        dependsOn.get(act).get(owner).removeAll(GetAttributes(owner));
      }
    }
    /* Then remove the agent itself */
    dependents.remove(owner);
  }

  /**
   * Get all dependents of a certain attribute owned by a specific agent. This can be used to
   * determine which actions need to be reconsidered when an attribute changes.
   *
   * @param attribute The attribute whose dependents are requested.
   * @param owner The owner of the attribute.
   * @return All dependents of the given attribute.
   */
  public HashSet<Action> GetDependents(String attribute, Agent owner) {
    HashMap<String, HashSet<Action>> d = dependents.get(owner);
    if (d != null) {
      return d.get(attribute);
    } else {
      return new HashSet<>(0);
    }
  }

  /**
   * Get all attributes owned by the given agent.
   *
   * @param owner The agent to query for attributes.
   * @return A set of all the owners attributes known to the graph.
   */
  public Set<String> GetAttributes(Agent owner) {
    HashMap<String, HashSet<Action>> atts = dependents.get(owner);
    if (atts != null) {
      return dependents.get(owner).keySet();
    } else {
      return new HashSet<>(0);
    }
  }

  /** Print all dependents of attributes of agents. */
  public void printAttributeDependents() {
    System.out.println("Attribute Dependencies:");
    for (Agent agent :
        dependents.keySet().stream()
            .sorted((x, y) -> x.getId() - y.getId())
            .collect(Collectors.toList())) {
      for (String attribute : dependents.get(agent).keySet()) {
        System.out.println(attribute + " of agent " + agent + " has the dependents:");
        for (Action action : dependents.get(agent).get(attribute)) {
          System.out.println("\t" + action + ".");
        }
      }
    }
    System.out.println("End");
  }

  /** Print dependencies for each action. */
  public void printActionDependencies() {
    System.out.println("Attribute Dependencies:");
    for (Action action :
        dependsOn.keySet().stream()
            .sorted((x, y) -> x.getOwner().getId() - y.getOwner().getId())
            .collect(Collectors.toList())) {
      System.out.println(action + " depends on:");
      for (Agent agent : dependsOn.get(action).keySet()) {
        for (String attribute : dependsOn.get(action).get(agent)) {
          System.out.println("\t" + attribute + " of agent " + agent + ".");
        }
      }
    }
    System.out.println("End");
  }

  /** Remove all data from the graph. */
  public void clearAllDependencies() {
    this.dependents = new HashMap<>();
    this.dependsOn = new HashMap<>();
  }
}
