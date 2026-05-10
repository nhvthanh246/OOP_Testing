package com.ecosystem.models;

/**
 * Omnivore represents an animal that can eat both plants and herbivores.
 * This heavily demonstrates Polymorphism, as the `canEat` method
 * allows multiple types of `IEdible`.
 */
public class Omnivore extends Animal implements IEdible {
    private static final double REPRODUCTION_THRESHOLD = 100.0;

    public Omnivore(int x, int y, double initialEnergy) {
        super(x, y, initialEnergy, 120.0, 2);
    }

    @Override
    public boolean canEat(IEdible food) {
        // Polymorphism: Omnivores accept both Plant and Herbivore objects!
        return food instanceof Plant || food instanceof Herbivore;
    }

    @Override
    public void act(Ecosystem ecosystem) {
        if (!isAlive())
            return;

        decayEnergy();
        if (!isAlive())
            return;

        if (isReadyToMove()) {
            IEdible food = ecosystem.findAdjacentFood(this);
            if (food != null && canEat(food)) {
                this.modifyEnergy(food.getNutritionalValue());
                food.beConsumed();
            } else {
                Organism nearestFood = ecosystem.findNearestFood(this, 5);
                if (nearestFood != null) {
                    ecosystem.moveToTarget(this, nearestFood);
                } else {
                    ecosystem.moveRandomly(this);
                }
            }
        }

        if (canReproduce()) {
            ecosystem.handleReproduction(this);
        }
    }

    // Omnivores can also be eaten by top predators like Carnivores if we allow it,
    // but right now Carnivores only eat Herbivores. We implement IEdible anyway.
    @Override
    public double getNutritionalValue() {
        return this.getEnergy() * 0.5;
    }

    @Override
    public void beConsumed() {
        this.die();
    }

    @Override
    public boolean canReproduce() {
        return this.getEnergy() >= REPRODUCTION_THRESHOLD;
    }

    @Override
    public Organism reproduce(int childX, int childY) {
        this.modifyEnergy(-50.0);
        return new Omnivore(childX, childY, 60.0);
    }
}
