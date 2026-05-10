package com.ecosystem.models;

/**
 * Inheritance: Plant inherits from Organism.
 * Multiple Interfaces: Implements IEdible and IReproducible.
 */
public class Plant extends Organism implements IEdible, IReproducible {
    public static final double GROWTH_RATE = 2.0;

    public Plant(int x, int y, double initialEnergy) {
        super(x, y, initialEnergy, 50.0);
    }

    @Override
    public void act(Ecosystem ecosystem) {
        if (!isAlive()) return;
        
        // Photosynthesis
        this.modifyEnergy(GROWTH_RATE);

        // Try to reproduce
        if (canReproduce()) {
            ecosystem.handleReproduction(this);
        }
    }

    // Polymorphism: Specific implementation for IEdible
    @Override
    public double getNutritionalValue() {
        return this.getEnergy() * 0.5; // Plants provide 50% of their energy to the eater
    }

    @Override
    public void beConsumed() {
        this.die(); // Plant dies when eaten
    }

    // Polymorphism: Specific implementation for IReproducible
    @Override
    public boolean canReproduce() {
        return this.getEnergy() >= getMaxEnergy();
    }

    @Override
    public Organism reproduce(int childX, int childY) {
        this.modifyEnergy(-20.0); // Cost of reproduction
        return new Plant(childX, childY, 20.0);
    }
}
