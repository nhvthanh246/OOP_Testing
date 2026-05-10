package com.ecosystem.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Encapsulation: The Ecosystem hides its internal grid and list structures.
 * It provides strict APIs for Organisms to interact without exposing raw data.
 */
public class Ecosystem {
    private int width;
    private int height;

    // Encapsulated: Private data structures
    private Organism[][] grid;
    private List<Organism> organisms;
    private List<EcosystemObserver> observers; // OBSERVER PATTERN

    private int carryingCapacity = 400;
    private double spawnRate = 0.3;

    public void setSpawnRate(double spawnRate) {
        this.spawnRate = spawnRate;
    }

    public void setCarryingCapacity(int carryingCapacity) {
        this.carryingCapacity = carryingCapacity;
    }

    public Ecosystem(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Organism[width][height];
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
        if (Math.random() < spawnRate && getPlantPopulation() < carryingCapacity) {
            spawnRandomPlant();
        }
        
        // Safety net: never let plants go completely extinct if capacity allows
        if (getPlantPopulation() < 10) {
            spawnRandomPlant();
        }

        // Absolute Extinction Prevention (Lotka-Volterra baseline guarantee)
        if (getHerbivorePopulation() < 2 && getPlantPopulation() > 10) {
            spawnRandom(Herbivore.class);
        }
        if (getCarnivorePopulation() < 2 && getHerbivorePopulation() > 5) {
            spawnRandom(Carnivore.class);
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

    public IEdible findAdjacentFood(Animal hunter) {
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
        for (int[] d : directions) {
            int nx = hunter.getX() + d[0];
            int ny = hunter.getY() + d[1];
            if (isValid(nx, ny) && grid[nx][ny] instanceof IEdible) {
                IEdible food = (IEdible) grid[nx][ny];
                if (hunter.canEat(food)) {
                    return food;
                }
            }
        }
        return null;
    }

    public Organism findNearestFood(Animal hunter, int radius) {
        Organism nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Organism org : organisms) {
            if (org instanceof IEdible && org.isAlive()) {
                if (hunter.canEat((IEdible) org)) {
                    double dist = Math.hypot(org.getX() - hunter.getX(), org.getY() - hunter.getY());
                    if (dist <= radius && dist < minDistance) {
                        minDistance = dist;
                        nearest = org;
                    }
                }
            }
        }
        return nearest;
    }

    public Organism findNearestPredator(Animal prey, int radius) {
        Organism nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Organism org : organisms) {
            if (org instanceof Carnivore && org.isAlive()) {
                double dist = Math.hypot(org.getX() - prey.getX(), org.getY() - prey.getY());
                if (dist <= radius && dist < minDistance) {
                    minDistance = dist;
                    nearest = org;
                }
            }
        }
        return nearest;
    }

    public void moveToTarget(Animal animal, Organism target) {
        int dx = Integer.compare(target.getX(), animal.getX());
        int dy = Integer.compare(target.getY(), animal.getY());
        int newX = animal.getX() + dx;
        int newY = animal.getY() + dy;

        if (isEmpty(newX, newY)) {
            moveOrganism(animal, newX, newY);
        } else {
            moveRandomly(animal);
        }
    }

    public void moveAwayFromTarget(Animal animal, Organism target) {
        int dx = Integer.compare(animal.getX(), target.getX());
        int dy = Integer.compare(animal.getY(), target.getY());
        int newX = animal.getX() + dx;
        int newY = animal.getY() + dy;

        if (isEmpty(newX, newY)) {
            moveOrganism(animal, newX, newY);
        } else {
            moveRandomly(animal);
        }
    }

    public void moveRandomly(Animal animal) {
        List<int[]> emptyCells = getAdjacentEmptyCells(animal.getX(), animal.getY());
        if (!emptyCells.isEmpty()) {
            int[] newPos = emptyCells.get((int) (Math.random() * emptyCells.size()));
            moveOrganism(animal, newPos[0], newPos[1]);
        }
    }

    public void handleReproduction(Organism parent) {
        if (!(parent instanceof IReproducible))
            return;

        if (parent instanceof Plant) {
            if (getPlantPopulation() >= carryingCapacity) return;
            
            // Wind Dispersal for Plants (scatter anywhere on map)
            int randX = (int) (Math.random() * width);
            int randY = (int) (Math.random() * height);
            
            IReproducible rep = (IReproducible) parent;
            Organism child = rep.reproduce(randX, randY); // Energy is deducted from parent
            
            // 50% chance the seed germinates successfully, AND the spot must be empty
            if (Math.random() < 0.5 && isEmpty(randX, randY)) {
                addOrganism(child);
            }
        } else {
            // Animals still reproduce in adjacent cells
            IReproducible rep = (IReproducible) parent;
            List<int[]> emptyCells = getAdjacentEmptyCells(parent.getX(), parent.getY());
            if (!emptyCells.isEmpty()) {
                int[] newPos = emptyCells.get((int) (Math.random() * emptyCells.size()));
                Organism child = rep.reproduce(newPos[0], newPos[1]);
                addOrganism(child);
            }
        }
    }

    // --- INTERNAL GRID MANAGEMENT ---

    public void addOrganism(Organism org) {
        if (isValid(org.getX(), org.getY()) && isEmpty(org.getX(), org.getY())) {
            grid[org.getX()][org.getY()] = org;
            organisms.add(org);
        }
    }

    private void removeOrganism(Organism org) {
        if (isValid(org.getX(), org.getY()) && grid[org.getX()][org.getY()] == org) {
            grid[org.getX()][org.getY()] = null;
        }
        organisms.remove(org);
    }

    private void moveOrganism(Organism org, int newX, int newY) {
        if (isValid(newX, newY) && isEmpty(newX, newY)) {
            grid[org.getX()][org.getY()] = null;
            if (org instanceof IMovable) {
                ((IMovable) org).move(newX, newY);
            }
            grid[newX][newY] = org;
        }
    }

    public boolean isEmpty(int x, int y) {
        return isValid(x, y) && grid[x][y] == null;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private List<int[]> getAdjacentEmptyCells(int x, int y) {
        List<int[]> emptyCells = new ArrayList<>();
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
        for (int[] d : directions) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (isEmpty(nx, ny)) {
                emptyCells.add(new int[] { nx, ny });
            }
        }
        return emptyCells;
    }

    private void spawnRandomPlant() {
        int x = (int) (Math.random() * width);
        int y = (int) (Math.random() * height);
        if (isEmpty(x, y)) {
            // FACTORY PATTERN: Create Organisms using Factory instead of 'new'
            addOrganism(OrganismFactory.createOrganism(Plant.class, x, y));
        }
    }

    private void spawnRandom(Class<? extends Organism> type) {
        int x = (int) (Math.random() * width);
        int y = (int) (Math.random() * height);
        if (isEmpty(x, y)) {
            addOrganism(OrganismFactory.createOrganism(type, x, y));
        }
    }

    // --- SAFE EXTERNAL APIs (Getters) ---

    // Encapsulation: Returns unmodifiable list to strictly enforce encapsulation
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
        return (int) organisms.stream().filter(o -> o instanceof Omnivore).count();
    }

    public void clear() {
        organisms.clear();
        grid = new Organism[width][height];
        notifyObservers();
    }
}
