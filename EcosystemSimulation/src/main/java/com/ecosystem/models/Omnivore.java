package com.ecosystem.models;

/**
 * Omnivore represents an animal that can eat both plants and herbivores.
 * This heavily demonstrates Polymorphism, as the `canEat` method
 * allows multiple types of `IEdible`.
 */
public class Omnivore extends Animal implements IEdible {
    private static final double REPRODUCTION_THRESHOLD = 100.0;

    public Omnivore(int x, int y, double initialVitality) {
        super(x, y, initialVitality, 120.0, 100.0, 100.0, 2);
    }

    @Override
    public boolean canEat(IEdible food) {
        // Polymorphism: Omnivores accept both Plant and Herbivore objects!
        if (this.getStamina() >= this.getMaxStamina() * 0.9)
            return false;
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
                // Omnivore bites the food
                double nutritionEaten = food.beBitten(40.0);
                this.modifyStamina(nutritionEaten);

                if (!((Organism) food).isAlive()) {
                    ecosystem.removeOrganism((Organism) food);
                    ecosystem.moveToTarget(this, (Organism) food);
                }
            } else {
                Organism nearestFood = ecosystem.findNearestFood(this, 100);
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
    public double beBitten(double biteSize) {
        double actualBite = Math.min(this.getEnergy(), biteSize);
        this.modifyEnergy(-actualBite);
        if (this.getEnergy() <= 0) {
            this.die();
        }
        return actualBite;
    }

    @Override
    public boolean canReproduce() {
        double currentVit = this.getEnergy() / this.getMaxEnergy();
        double lastVit = this.getLastVitalityPercentage();

        double[] thresholds = { 0.6, 0.4 };
        for (double t : thresholds) {
            if (lastVit > t && currentVit <= t) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Organism reproduce(int childX, int childY) {
        this.modifyStamina(-30.0);
        return new Omnivore(childX, childY, 120.0);
    }
}
