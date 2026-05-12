package com.ecosystem.models;

/**
 * Abstraction: Interface for organisms capable of reproducing.
 */
public interface IReproducible {
    boolean canReproduce();

    Organism reproduce(double childX, double childY);
}
