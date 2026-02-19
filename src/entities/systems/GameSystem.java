package entities.systems;

import entities.EntityManager;

public interface GameSystem {
    void update(EntityManager entities, double dt);
}
