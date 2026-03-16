package entities.systems;

import entities.Entity;
import entities.EntityManager;
import entities.components.ScriptComponent;

public class ScriptSystem implements GameSystem {

	@Override
	public void update(EntityManager entityManager, double dt) {
		for (Entity e : entityManager.getEntities()) {
			if (e.has(ScriptComponent.class)) {
				e.get(ScriptComponent.class).update(e, entityManager, dt);
			}
		}
	}

}
