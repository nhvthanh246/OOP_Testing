package com.ecosystem.models;

/**
 * Abstraction: Interface for organisms capable of reproducing.
 */
public interface IReproducible {
    boolean canReproduce();

    Organism reproduce(int childX, int childY);
}
