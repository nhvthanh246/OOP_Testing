package com.ecosystem.models;

/**
 * Abstraction: Interface for entities that can change their position.
 */
public interface IMovable {
    void move(int targetX, int targetY);
}
