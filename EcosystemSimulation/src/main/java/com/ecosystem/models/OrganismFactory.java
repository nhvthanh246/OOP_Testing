package com.ecosystem.models;

/**
 * Factory Design Pattern: Encapsulates the instantiation logic of organisms.
 * Ecosystem calls this factory instead of using 'new' directly.
 */
public class OrganismFactory {

    public static Organism createOrganism(Class<? extends Organism> type, double x, double y) {
        if (type == Plant.class) {
            return new Plant(x, y, 50.0);
        } else if (type == Herbivore.class) {
            // Initial spawn is full HP and MP
            return new Herbivore(x, y, 400.0, 200.0);
        } else if (type == Carnivore.class) {
            // Initial spawn is full HP and MP
            return new Carnivore(x, y, 600.0, 300.0);
        }
        throw new IllegalArgumentException("Unknown organism type: " + type.getName());
    }
}
