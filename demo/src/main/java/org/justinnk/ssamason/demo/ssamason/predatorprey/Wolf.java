/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.ssamason.demo.ssamason.predatorprey;

import java.util.List;
import java.util.stream.Collectors;
import org.justinnk.ssamason.extension.Action;
import org.justinnk.ssamason.extension.NoAgentAttribute;
import sim.engine.SimState;
import sim.field.network.Edge;
import sim.util.Bag;

public class Wolf extends Species {

  @NoAgentAttribute public static final int maxHunger = 3;

  /* effectively constant and thus no agent attribute. */
  @NoAgentAttribute public Hobby hobby;

  public int hunger = 1;

  public int fertility = 1;

  @Override
  protected int getFriendRadius() {
    return 50;
  }

  public Wolf(SimState model) {
    super(model, ((PredatorPreyModel) model).wolves);
    PredatorPreyModel state = (PredatorPreyModel) model;

    /* select a random hobby when initialised. */
    this.hobby = Hobby.values()[model.random.nextInt(3)];

    this.addAction(
        new Action(
            () -> hasContactWith(this.hobby),
            () -> {
              log("Mating");
              /* add a new wolf at a random position to the grid and to the graph. */
              state.addNewWolf();
              /* increase the hunger when giving birth. */
              this.hunger += 1;
              /* because of the rate, fertility will always be >0 here */
              this.fertility -= 1;
            },
            () -> state.wolfReproduction * this.fertility,
            "Mate"));

    this.addAction(
        new Action(
            () -> true,
            () -> {
              log("Goes undercover");
              Sheep observed = pickSheepInCircumference();
              if (observed != null) {
                state.undercoverNet.addEdge(this, observed, 1.0);
                log("adds " + observed + " to whatchlist");
              }
            },
            () -> state.wolfGoUndercover,
            "GoUndercover"));

    this.addAction(
        new Action(
            () ->
                !hasContactWith(this.hobby)
                    || (this.hunger >= Wolf.maxHunger
                        && state.undercoverNet.getEdges(this, null).size() == 0),
            () -> {
              log("Dies");
              this.kill();
              state.world.remove(this);
              state.wolves.removeNode(this);
              state.undercoverNet.removeNode(this);
            },
            () -> state.wolfDie,
            "Die"));

    this.addAction(
        new Action(
            () -> this.hunger < Wolf.maxHunger,
            () -> {
              log("Hungers");
              this.hunger += 1;
            },
            () -> state.wolfHunger,
            "Hunger"));

    this.addAction(
        new Action(
            () -> this.hunger > 0,
            () -> {
              log("Hunts");
              Bag allSheep = state.undercoverNet.getEdges(this, null);
              /*
               * if there are sheep left, select a random one to kill, decrease hunger and
               * increase fertility.
               */
              if (!allSheep.isEmpty()) {
                int preyN = state.random.nextInt(allSheep.size());
                Sheep prey = (Sheep) ((Edge) allSheep.get(preyN)).getOtherNode(this);
                log("Kills " + prey);
                prey.kill();
                state.sheep.removeNode(prey);
                state.world.remove(prey);
                state.undercoverNet.removeNode(prey);
                this.hunger -= 1;
                this.fertility += 1;
              } else {
                this.hunger += 1;
              }
            },
            () -> state.wolfHunt * this.hunger * state.undercoverNet.getEdges(this, null).size(),
            "Hunt"));
  }

  private boolean hasContactWith(Hobby hobby) {
    Bag contacts = ((PredatorPreyModel) model).wolves.getEdges(this, null);
    int numContacts = contacts.size();
    for (int contact = 0; contact < numContacts; contact++) {
      Wolf contactPerson = (Wolf) ((Edge) contacts.get(contact)).getOtherNode(this);
      Hobby contactHobby = contactPerson.hobby;
      if (contactHobby == this.hobby) {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  private Sheep pickSheepInCircumference() {
    PredatorPreyModel state = (PredatorPreyModel) model;
    /* query wold for neighbours within distance */
    Bag neighbors =
        new Bag(
            state.world.getNeighborsExactlyWithinDistance(state.world.getObjectLocation(this), 30));
    if (!neighbors.isEmpty()) {
      /* get the existing connections */
      Bag existingConnections = new Bag(state.undercoverNet.getEdges(this, null));
      List<Sheep> alreadyObservedSheep =
          (List<Sheep>)
              existingConnections.stream()
                  .map(e -> ((Edge) e).getOtherNode(this))
                  .collect(Collectors.toList());
      /* get all neighbouring sheep that are not already observed */
      List<Sheep> potentialNewPrey =
          (List<Sheep>)
              neighbors.stream()
                  .filter(s -> s instanceof Sheep && !alreadyObservedSheep.contains(s))
                  .collect(Collectors.toList());
      if (!potentialNewPrey.isEmpty()) {
        /* pick a random sheep as prey */
        return potentialNewPrey.get(state.random.nextInt(potentialNewPrey.size()));
      }
    }
    /* if no prey was found */
    return null;
  }

  @Override
  public String toString() {
    /* add the hobby to the display name */
    if (this.hobby != null) {
      return "" + this.getId() + " (" + this.hobby.toString() + ")";
    }
    return String.valueOf(this.getId());
  }
}
