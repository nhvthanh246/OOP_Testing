package com.ecosystem.models;

public class Herbivore extends Animal implements IEdible {
    private static final double REPRODUCTION_THRESHOLD = 95.0;

    public Herbivore(int x, int y, double initialEnergy) {
        super(x, y, initialEnergy, 100.0, 1);
    }

    @Override
    public boolean canEat(IEdible food) {
        // Polymorphism: Herbivore diet strategy
        if (this.getEnergy() >= this.getMaxEnergy() * 0.9) return false;
        return food instanceof Plant;
    }

    @Override
    public void act(Ecosystem ecosystem) {
        if (!isAlive())
            return;

        decayEnergy();
        if (!isAlive())
            return;

        if (isReadyToMove()) {
            // Evasion Logic: Run from predators first!
            Organism predator = ecosystem.findNearestPredator(this, 5);
            // 60% chance to successfully flee. Otherwise, panic (freeze or find food)
            if (predator != null && Math.random() < 0.6) {
                ecosystem.moveAwayFromTarget(this, predator);
            } else {
                // Find food using Ecosystem's encapsulated API
                IEdible food = ecosystem.findAdjacentFood(this);
                if (food != null && canEat(food)) {
                    this.modifyEnergy(food.getNutritionalValue());
                    food.beConsumed();
                } else {
                    // Move towards nearest food
                    Organism nearestFood = ecosystem.findNearestFood(this, 6);
                    if (nearestFood != null) {
                        ecosystem.moveToTarget(this, nearestFood);
                    } else {
                        ecosystem.moveRandomly(this);
                    }
                }
            }
        }

        // Try to reproduce
        if (canReproduce()) {
            ecosystem.handleReproduction(this);
        }
    }

    // --- IEdible Implementation (Carnivores eat Herbivores) ---
    @Override
    public double getNutritionalValue() {
        return this.getEnergy() * 0.5;
    }

    @Override
    public void beConsumed() {
        this.die();
    }

    // --- IReproducible Implementation ---
    @Override
    public boolean canReproduce() {
        return this.getEnergy() >= REPRODUCTION_THRESHOLD;
    }

    @Override
    public Organism reproduce(int childX, int childY) {
        this.modifyEnergy(-50.0);
        return new Herbivore(childX, childY, 50.0);
    }
}
