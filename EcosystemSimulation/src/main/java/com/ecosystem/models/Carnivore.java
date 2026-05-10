package com.ecosystem.models;

public class Carnivore extends Animal {
    private static final double REPRODUCTION_THRESHOLD = 120.0;

    public Carnivore(int x, int y, double initialEnergy) {
        super(x, y, initialEnergy, 150.0, 1);
    }

    @Override
    public boolean canEat(IEdible food) {
        // Polymorphism: Carnivore diet strategy
        // Satiety Logic: Don't kill if already full
        if (this.getEnergy() >= this.getMaxEnergy() * 0.8)
            return false;
        return food instanceof Herbivore;
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
                Organism nearestFood = ecosystem.findNearestFood(this, 8);
                if (nearestFood != null) {
                    ecosystem.moveToTarget(this, nearestFood);
                } else {
                    ecosystem.moveRandomly(this);
                }
            }
        }

        // Reproduce
        if (canReproduce()) {
            ecosystem.handleReproduction(this);
        }
    }

    @Override
    public boolean canReproduce() {
        return this.getEnergy() >= REPRODUCTION_THRESHOLD;
    }

    @Override
    public Organism reproduce(int childX, int childY) {
        this.modifyEnergy(-60.0);
        return new Carnivore(childX, childY, 90.0);
    }
}
