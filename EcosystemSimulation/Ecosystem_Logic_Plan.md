# Ecosystem Simulation Logic Plan

## 1. Overview
The system is a 2D ecosystem simulation based on the Predator-Prey model. It includes three main entities: Plants (Herb), Herbivores (Hare), and Carnivores (Wolf). The simulation runs continuously, updating entity states, handling interactions (hunting/eating), reproduction, and death.

## 2. Entities & Attributes

### 2.1. Plant (Herb)
- **Attributes**: Position (x, y).
- **Behavior**: Spawns randomly on the map over time. Serves as food for Hares.

### 2.2. Herbivore (Hare)
- **Attributes**: 
  - Max HP: 400
  - Max MP (Energy/Hunger): 200
  - Velocity: 0.8
  - Position (x, y)
  - Target angle/direction (t)
- **Behavior**: Consumes Plants to restore MP.

### 2.3. Carnivore (Wolf)
- **Attributes**: 
  - Max HP: 600
  - Max MP (Energy/Hunger): 300
  - Velocity: 0.8
  - Position (x, y)
  - Target angle/direction (t)
- **Behavior**: Hunts and consumes Hares to restore MP.

## 3. Core Mechanics

### 3.1. Movement & Pathfinding
- **Wandering**: Entities slightly change their direction angle randomly every frame.
- **Hunting/Foraging**: 
  - If a Hare's MP drops below 70%, it finds the nearest Plant. If the Plant is within a specific distance (30 units), the Hare eats it.
  - If a Wolf's MP drops below 50% (and Wolf population is less than half of Hare population), it finds the nearest Hare. If within 20 units, the Wolf eats the Hare.
- Entities are constrained within the map boundaries.

### 3.2. Vital Signs (HP & MP)
- Both HP and MP continuously decrease by 1 unit per frame for both Hares and Wolves.
- **Eating**: Restores MP to its maximum value.
- **Death**: If either HP or MP reaches 0, the entity dies and is removed from the simulation.

### 3.3. Reproduction
- Reproduction occurs when an entity meets specific HP and MP thresholds.
- **Hare Reproduction**: Happens when MP > 50% and HP hits specific interval milestones (e.g., 20%, 40%, 50%, 60%, 80% of max HP).
- **Wolf Reproduction**: Happens when MP > 50% and HP hits specific milestones (e.g., 40%, 60% of max HP).
- Newborns spawn at the parent's location with full HP and 50% MP.

### 3.4. Environment Spawn Rules
- Plants (Herbs) automatically spawn 1 unit every 5 frames in random locations (ensuring they aren't clustered too closely to other plants).

## 4. UI & Tracking
- **Adjustments**: Sliders to manually adjust the population of Plants, Hares, and Wolves.
- **Graphing**: The system tracks the population sizes every 30 frames and plots them on a line graph to visualize the ecosystem balance over time.
- **Interaction**: Users can drag and drop Hares and Wolves using the mouse/touch.