# Ecosystem Simulation Logic Refactoring Walkthrough

I have completely refactored the ecosystem simulation from an integer grid-based framework into a continuous 2D coordinate space, perfectly adhering to the logic constraints outlined in the `Ecosystem_Logic_Plan.md`. 

Here is a summary of all the changes made:

## Core Refactoring

1. **Continuous Coordinates**: `Organism.java` was modified to use `double x` and `double y` instead of integers. I replaced the abstract `energy` and `stamina` fields with explicit `hp` and `mp` fields as requested. 
2. **Movement Dynamics**: `Animal.java` no longer moves step-by-step between grid cells using an integer cooldown. Instead, animals now have an `angle` (in radians) and `velocity` (set to 0.8 pixels/frame). They move via trigonometric projection (`Math.cos()` and `Math.sin()`) and naturally "bounce" off the map boundaries.
3. **Grid Removal**: `Ecosystem.java` was entirely stripped of the `Organism[][] grid`. All distance, collision, and proximity checks (e.g., `findNearestPlant`, `findNearestHare`) now rely on pure Euclidean distance calculations (`Math.hypot`).

## Entity Implementations

* **Herbivore (Hare)**: Operates with 400 Max HP, 200 Max MP, and 0.8 velocity. When its MP falls below 70% (140), it searches the map for the nearest `Plant`. If the plant is within 30 pixels, it is completely eaten, restoring the Hare's MP to maximum. Hares constantly wander with slight angular jitter, lose 1 HP and 1 MP per frame, and reproduce dynamically at 5 distinct HP milestones (if MP > 50%).
* **Carnivore (Wolf)**: Operates with 600 Max HP, 300 Max MP, and 0.8 velocity. Wolves only hunt when their population is less than half the Hare population AND their MP drops below 50% (150). They eat Hares within 20 pixels. They reproduce dynamically at 2 distinct HP milestones.
* **Plant (Herb)**: Now acts as a static entity. Plants no longer expend MP/HP or grow dynamically. Instead, `Ecosystem.java` dictates an environmental spawn of 1 Plant every 5 frames.

## Cleanup & UI Changes

> [!TIP]
> **View Rendering Adaptations**
> `SimulationController.java` was heavily updated to support floating-point mathematics. It now spawns entities and draws their UI representations based on exact pixel coordinates instead of grid cells.

- **Removed Omnivore.java & IEdible.java**: These unused interfaces and classes were safely purged from the codebase to align with the strictly 3-entity model.
- **Factory Updates**: `OrganismFactory.java` was synchronized to initialize Hares and Wolves with maximum HP and MP upon starting the simulation.

You can run your main application as you usually do to observe the brand-new continuous ecosystem interacting fluidly!
