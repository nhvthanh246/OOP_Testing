package com.ecosystem.models;

public class Carnivore extends Animal {

    public Carnivore(double x, double y, double hp, double mp) {
        super(x, y, hp, 600.0, mp, 300.0, 0.8);
    }

    @Override
    public boolean canEat(IEdible food) { return false; } // Deprecated

    @Override
    public void act(Ecosystem ecosystem) {
        if (!isAlive()) return;

        decay();
        if (!isAlive()) return;

        boolean shouldHunt = ecosystem.getCarnivorePopulation() < ecosystem.getHerbivorePopulation() / 2.0;

        if (shouldHunt && this.getMp() < this.getMaxMp() * 0.5) {
            Herbivore preyNear = ecosystem.findNearestHare(this, 20.0);
            if (preyNear != null) {
                preyNear.die();
                ecosystem.removeOrganism(preyNear);
                this.modifyMp(this.getMaxMp());
            } else {
                Herbivore nearestPrey = ecosystem.findNearestHare(this, Double.MAX_VALUE);
                if (nearestPrey != null) {
                    ecosystem.moveTowards(this, nearestPrey);
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

        double[] thresholds = {0.6, 0.4};
        for (double t : thresholds) {
            if (lastVit > t && currentVit <= t) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Organism reproduce(double childX, double childY) {
        return new Carnivore(childX, childY, 600.0, 150.0);
    }
}
