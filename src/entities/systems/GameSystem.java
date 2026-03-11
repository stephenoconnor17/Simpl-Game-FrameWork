package entities.systems;

import entities.EntityManager;

public interface GameSystem {
    void update(EntityManager entityManager, double dt);
}
