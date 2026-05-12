package com.ecosystem.models;

/**
 * Inheritance: Plant inherits from Organism.
 * Multiple Interfaces: Implements IReproducible.
 */
public class Plant extends Organism implements IReproducible {

    public Plant(double x, double y, double initialHp) {
        super(x, y, initialHp, 50.0, 0, 0); // Plants don't use MP
    }

    @Override
    public void act(Ecosystem ecosystem) {
        if (!isAlive())
            return;

        // In the new logic, Plants serve merely as food and don't grow/decay
        // Spawning is handled entirely by Ecosystem.java every 5 frames.
    }

    // Polymorphism: Specific implementation for IReproducible
    @Override
    public boolean canReproduce() {
        return false; // Reproduction handled by Ecosystem spawning
    }

    @Override
    public Organism reproduce(double childX, double childY) {
        return new Plant(childX, childY, 20.0);
    }
}
