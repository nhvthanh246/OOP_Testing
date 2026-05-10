package com.ecosystem.models;

/**
 * Factory Design Pattern: Encapsulates the instantiation logic of organisms.
 * Ecosystem calls this factory instead of using 'new' directly.
 */
public class OrganismFactory {

    public static Organism createOrganism(Class<? extends Organism> type, int x, int y) {
        if (type == Plant.class) {
            return new Plant(x, y, 20.0);
        } else if (type == Herbivore.class) {
            return new Herbivore(x, y, 50.0);
        } else if (type == Carnivore.class) {
            return new Carnivore(x, y, 90.0);
        } else if (type == Omnivore.class) {
            return new Omnivore(x, y, 70.0);
        }
        throw new IllegalArgumentException("Unknown organism type: " + type.getName());
    }
}
