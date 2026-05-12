package com.ecosystem.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Encapsulation: The Ecosystem hides its internal structures.
 * It provides strict APIs for Organisms to interact without exposing raw data.
 * Now refactored for continuous 2D coordinates.
 */
public class Ecosystem {
    private double width;
    private double height;

    private List<Organism> organisms;
    private List<EcosystemObserver> observers; // OBSERVER PATTERN

    private int carryingCapacity = 200;
    private double spawnRate = 1.0;
    private int frameCounter = 0;

    public void setSpawnRate(double spawnRate) {
        this.spawnRate = spawnRate;
    }

    public void setCarryingCapacity(int carryingCapacity) {
        this.carryingCapacity = carryingCapacity;
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public Ecosystem(double width, double height) {
        this.width = width;
        this.height = height;
        this.organisms = new CopyOnWriteArrayList<>();
        this.observers = new ArrayList<>();
    }

    // --- OBSERVER PATTERN ---
    public void addObserver(EcosystemObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void notifyObservers() {
        int p = getPlantPopulation();
        int h = getHerbivorePopulation();
        int c = getCarnivorePopulation();
        int o = getOmnivorePopulation();
        for (EcosystemObserver obs : observers) {
            obs.onPopulationChanged(p, h, c, o);
        }
    }

    public void update() {
        frameCounter++;

        // Plants (Herbs) automatically spawn 1 unit every 5 frames in random locations
        if (frameCounter % 5 == 0) {
            int spawnAmount = Math.max(1, (int) spawnRate);
            for (int i = 0; i < spawnAmount; i++) {
                if (getPlantPopulation() < carryingCapacity) {
                    spawnRandomPlant();
                }
            }
        }

        // Safety net: ensure baseline grass
        if (getPlantPopulation() < 50) {
            spawnRandomPlant();
        }

        for (Organism org : organisms) {
            if (org.isAlive()) {
                org.act(this);
            } else {
                removeOrganism(org);
            }
        }

        notifyObservers(); // Notify UI after tick
    }

    // --- ENCAPSULATED API FOR ORGANISMS ---

    public Plant findNearestPlant(Animal hunter, double radius) {
        Plant nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Organism org : organisms) {
            if (org instanceof Plant && org.isAlive()) {
                double dist = Math.hypot(org.getX() - hunter.getX(), org.getY() - hunter.getY());
                if (dist <= radius && dist < minDistance) {
                    minDistance = dist;
                    nearest = (Plant) org;
                }
            }
        }
        return nearest;
    }

    public Herbivore findNearestHare(Animal hunter, double radius) {
        Herbivore nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Organism org : organisms) {
            if (org instanceof Herbivore && org.isAlive()) {
                double dist = Math.hypot(org.getX() - hunter.getX(), org.getY() - hunter.getY());
                if (dist <= radius && dist < minDistance) {
                    minDistance = dist;
                    nearest = (Herbivore) org;
                }
            }
        }
        return nearest;
    }

    public void moveTowards(Animal animal, Organism target) {
        double dx = target.getX() - animal.getX();
        double dy = target.getY() - animal.getY();
        double angle = Math.atan2(dy, dx);
        animal.setAngle(angle);
        animal.moveForward(this);
    }

    public void wander(Animal animal) {
        // slightly change direction angle randomly every frame
        double angleChange = (Math.random() - 0.5) * 0.5; // +/- 0.25 radians
        animal.setAngle(animal.getAngle() + angleChange);
        animal.moveForward(this);
    }

    public void handleReproduction(Organism parent) {
        if (!(parent instanceof IReproducible))
            return;

        if (parent instanceof Plant) {
            if (getPlantPopulation() >= carryingCapacity)
                return;

            // Wind Dispersal for Plants (scatter anywhere on map)
            double randX = Math.random() * width;
            double randY = Math.random() * height;

            IReproducible rep = (IReproducible) parent;
            Organism child = rep.reproduce(randX, randY);
            addOrganism(child);
        } else {
            // Animals reproduce at the parent's exact location as per logic plan
            IReproducible rep = (IReproducible) parent;
            Organism child = rep.reproduce(parent.getX(), parent.getY());
            addOrganism(child);
        }
    }

    // --- INTERNAL MANAGEMENT ---

    public void addOrganism(Organism org) {
        if (isValid(org.getX(), org.getY())) {
            organisms.add(org);
        }
    }

    public void removeOrganism(Organism org) {
        organisms.remove(org);
    }

    public void dragOrganism(Organism org, double newX, double newY) {
        if (isValid(newX, newY)) {
            org.setX(newX);
            org.setY(newY);
        }
    }

    public boolean isEmpty(double x, double y) {
        // Continuous map doesn't strictly have "empty" cells
        return true;
    }

    public boolean isValid(double x, double y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private void spawnRandomPlant() {
        double x = Math.random() * width;
        double y = Math.random() * height;
        addOrganism(OrganismFactory.createOrganism(Plant.class, x, y));
    }

    // --- SAFE EXTERNAL APIs (Getters) ---

    public List<Organism> getOrganisms() {
        return Collections.unmodifiableList(organisms);
    }

    public int getPlantPopulation() {
        return (int) organisms.stream().filter(o -> o instanceof Plant).count();
    }

    public int getHerbivorePopulation() {
        return (int) organisms.stream().filter(o -> o instanceof Herbivore).count();
    }

    public int getCarnivorePopulation() {
        return (int) organisms.stream().filter(o -> o instanceof Carnivore).count();
    }

    public int getOmnivorePopulation() {
        return 0; // Removed
    }

    public void clear() {
        organisms.clear();
        frameCounter = 0;
        notifyObservers();
    }
}
