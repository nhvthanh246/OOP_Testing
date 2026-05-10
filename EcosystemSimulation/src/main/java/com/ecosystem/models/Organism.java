package com.ecosystem.models;

/**
 * Abstraction: Abstract base class representing any living organism.
 * Encapsulation: State fields are hidden. Energy modification is controlled.
 */
public abstract class Organism {
    private int x;
    private int y;
    private double energy;
    private double maxEnergy;
    private boolean isAlive;

    public Organism(int x, int y, double energy, double maxEnergy) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.maxEnergy = maxEnergy;
        this.isAlive = true;
    }

    // Abstraction & Polymorphism
    public abstract void act(Ecosystem ecosystem);

    // Encapsulation: Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getEnergy() {
        return energy;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }

    public boolean isAlive() {
        return isAlive;
    }

    // Encapsulation: Protected Setters and Mutators (Only accessible within
    // inheritance tree / package)
    protected void setX(int x) {
        this.x = x;
    }

    protected void setY(int y) {
        this.y = y;
    }

    protected void modifyEnergy(double amount) {
        if (!isAlive)
            return;
        this.energy += amount;
        if (this.energy > maxEnergy) {
            this.energy = maxEnergy;
        } else if (this.energy <= 0) {
            this.energy = 0;
            this.die();
        }
    }

    protected void die() {
        this.isAlive = false;
    }
}
