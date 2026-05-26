package entities.systems;

import entities.EntityManager;

/** Common interface for all update-phase systems. */
public interface GameSystem {
    void update(EntityManager entityManager, double dt);
}
