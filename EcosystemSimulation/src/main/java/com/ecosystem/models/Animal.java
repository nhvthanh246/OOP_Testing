package com.ecosystem.models;

/**
 * Inheritance: Animal extends Organism.
 * Multiple Interfaces: Implements IMovable and IReproducible.
 */
public abstract class Animal extends Organism implements IMovable, IReproducible {
    private int speed;
    private int movementCooldown;

    public Animal(int x, int y, double energy, double maxEnergy, int speed) {
        super(x, y, energy, maxEnergy);
        this.speed = speed;
        this.movementCooldown = speed;
    }

    // Polymorphism: Each animal type will define what it considers edible.
    public abstract boolean canEat(IEdible food);

    @Override
    public void move(int targetX, int targetY) {
        this.setX(targetX);
        this.setY(targetY);
    }

    protected void decayEnergy() {
        this.modifyEnergy(-1.0); // Metaboic cost
    }
    
    // Encapsulation: controls movement ticks internally
    public boolean isReadyToMove() {
        movementCooldown--;
        if (movementCooldown <= 0) {
            movementCooldown = speed;
            return true;
        }
        return false;
    }
}
