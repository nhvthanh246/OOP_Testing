package com.ecosystem.models;

/**
 * Abstraction: Abstract base class representing any living organism.
 * Encapsulation: State fields are hidden. Energy modification is controlled.
 */
public abstract class Organism {
    private double x;
    private double y;
    private double hp;
    private double maxHp;
    private double mp;
    private double maxMp;
    private double angle;
    private boolean isAlive;

    // Visual State for Smooth Rendering
    private double visualX;
    private double visualY;

    public Organism(double x, double y, double hp, double maxHp, double mp, double maxMp) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = maxHp;
        this.mp = mp;
        this.maxMp = maxMp;
        this.angle = Math.random() * 2 * Math.PI; // Random initial angle
        this.isAlive = true;
        
        // Initial visual position matches logical position
        this.visualX = x;
        this.visualY = y;
    }

    // Smooth movement interpolation
    public void updateVisuals(double smoothingFactor) {
        visualX += (x - visualX) * smoothingFactor;
        visualY += (y - visualY) * smoothingFactor;
    }

    // Abstraction & Polymorphism
    public abstract void act(Ecosystem ecosystem);

    // Encapsulation: Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVisualX() { return visualX; }
    public double getVisualY() { return visualY; }
    public void setVisualX(double visualX) { this.visualX = visualX; }
    public void setVisualY(double visualY) { this.visualY = visualY; }

    public double getHp() { return hp; }
    public double getMaxHp() { return maxHp; }
    public double getMp() { return mp; }
    public double getMaxMp() { return maxMp; }
    public double getAngle() { return angle; }

    public boolean isAlive() { return isAlive; }

    // Encapsulation: Setters
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setAngle(double angle) { this.angle = angle; }

    public void modifyHp(double amount) {
        if (!isAlive) return;
        this.hp += amount;
        if (this.hp > maxHp) {
            this.hp = maxHp;
        } else if (this.hp <= 0) {
            this.hp = 0;
            this.die();
        }
    }

    public void modifyMp(double amount) {
        if (!isAlive) return;
        this.mp += amount;
        if (this.mp > maxMp) {
            this.mp = maxMp;
        } else if (this.mp <= 0) {
            this.mp = 0;
            this.die();
        }
    }

    public void die() {
        this.isAlive = false;
    }
}
