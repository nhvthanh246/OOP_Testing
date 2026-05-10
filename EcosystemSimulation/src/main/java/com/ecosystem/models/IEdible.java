package com.ecosystem.models;

/**
 * Abstraction: Interface to define objects that can be eaten by other organisms.
 */
public interface IEdible {
    double getNutritionalValue();
    void beConsumed();
}
