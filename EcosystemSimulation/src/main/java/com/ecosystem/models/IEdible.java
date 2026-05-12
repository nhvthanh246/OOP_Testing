package com.ecosystem.models;

/**
 * Abstraction: Interface to define objects that can be eaten by other organisms.
 */
public interface IEdible {
    /**
     * Called when an organism is bitten.
     * @param biteSize The amount of energy the hunter tries to bite off.
     * @return The actual amount of energy consumed (e.g., if the prey has less energy than biteSize).
     */
    double beBitten(double biteSize);
}
