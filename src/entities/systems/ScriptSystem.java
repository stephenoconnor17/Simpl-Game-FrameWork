package entities.systems;

import entities.Entity;
import entities.EntityManager;
import entities.components.Script;

public class ScriptSystem implements GameSystem {

	@Override
	public void update(EntityManager entityManager, double dt) {
		for (Entity e : entityManager.getEntities()) {
			if (e.has(Script.class)) {
				e.get(Script.class).update(e, entityManager, dt);
			}
		}
	}

}
