package com.ecosystem.models;

/**
 * Inheritance: Animal extends Organism.
 * Multiple Interfaces: Implements IMovable and IReproducible.
 */
public abstract class Animal extends Organism implements IMovable, IReproducible {
    private double velocity;
    private double lastHpPercentage;

    public Animal(double x, double y, double hp, double maxHp, double mp, double maxMp, double velocity) {
        super(x, y, hp, maxHp, mp, maxMp);
        this.velocity = velocity;
        this.lastHpPercentage = hp / maxHp;
    }

    // Polymorphism: Each animal type will define what it considers edible.
    public abstract boolean canEat(IEdible food);

    @Override
    public void moveForward(Ecosystem ecosystem) {
        if (!isAlive()) return;
        
        double newX = getX() + Math.cos(getAngle()) * velocity;
        double newY = getY() + Math.sin(getAngle()) * velocity;

        // Constrain to map boundaries and bounce
        if (newX < 0) {
            newX = 0;
            setAngle(Math.PI - getAngle());
        } else if (newX >= ecosystem.getWidth()) {
            newX = ecosystem.getWidth() - 1;
            setAngle(Math.PI - getAngle());
        }

        if (newY < 0) {
            newY = 0;
            setAngle(-getAngle());
        } else if (newY >= ecosystem.getHeight()) {
            newY = ecosystem.getHeight() - 1;
            setAngle(-getAngle());
        }

        setX(newX);
        setY(newY);
    }

    protected void decay() {
        // Record HP percentage before decay for breeding checks
        this.lastHpPercentage = this.getHp() / this.getMaxHp();
        
        this.modifyHp(-1.0);
        this.modifyMp(-1.0);
    }

    public double getVelocity() {
        return velocity;
    }

    public double getLastHpPercentage() {
        return lastHpPercentage;
    }
}
