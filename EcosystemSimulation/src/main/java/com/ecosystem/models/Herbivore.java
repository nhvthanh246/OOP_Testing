package com.ecosystem.models;

public class Herbivore extends Animal {

    public Herbivore(double x, double y, double hp, double mp) {
        super(x, y, hp, 400.0, mp, 200.0, 0.8);
    }

    @Override
    public boolean canEat(IEdible food) { return false; } // Deprecated

    @Override
    public void act(Ecosystem ecosystem) {
        if (!isAlive()) return;

        decay();
        if (!isAlive()) return;

        if (this.getMp() < this.getMaxMp() * 0.7) {
            Plant food = ecosystem.findNearestPlant(this, 30.0);
            if (food != null) {
                food.die();
                ecosystem.removeOrganism(food);
                this.modifyMp(this.getMaxMp()); // restore to full
            } else {
                Plant nearestFood = ecosystem.findNearestPlant(this, Double.MAX_VALUE);
                if (nearestFood != null) {
                    ecosystem.moveTowards(this, nearestFood);
                } else {
                    ecosystem.wander(this);
                }
            }
        } else {
            ecosystem.wander(this);
        }

        if (canReproduce()) {
            ecosystem.handleReproduction(this);
        }
    }

    @Override
    public boolean canReproduce() {
        if (this.getMp() <= this.getMaxMp() * 0.5) return false;

        double currentVit = this.getHp() / this.getMaxHp();
        double lastVit = this.getLastHpPercentage();

        double[] thresholds = {0.8, 0.6, 0.5, 0.4, 0.2};
        for (double t : thresholds) {
            if (lastVit > t && currentVit <= t) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Organism reproduce(double childX, double childY) {
        return new Herbivore(childX, childY, 400.0, 100.0); // Full HP, 50% MP
    }
}
