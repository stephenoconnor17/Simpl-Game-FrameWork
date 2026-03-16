package entities.components;

import entities.Entity;
import entities.EntityManager;

@FunctionalInterface
public interface Script {
	void update(Entity self, EntityManager entityManager, double dt);
}
