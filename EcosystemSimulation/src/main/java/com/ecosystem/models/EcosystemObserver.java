package com.ecosystem.models;

/**
 * Observer Design Pattern: Defines the interface for observing ecosystem
 * population changes.
 */
public interface EcosystemObserver {
    void onPopulationChanged(int plants, int herbivores, int carnivores, int omnivores);
}
