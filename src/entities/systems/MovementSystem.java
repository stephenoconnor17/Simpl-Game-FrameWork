package entities.systems;

import entities.Entity;
import entities.EntityManager;
import entities.components.movement.MovementValues;
import entities.components.transform.Position;

public class MovementSystem implements GameSystem {

	@Override
	public void update(EntityManager entityManager, double dt) {
		for (Entity e : entityManager.getEntities()) {
			if (e.has(Position.class) && e.has(MovementValues.class)) {
				Position pos = e.get(Position.class);
				MovementValues mov = e.get(MovementValues.class);

				pos.x += mov.velocityX * dt;
				pos.y += mov.velocityY * dt;
			}
		}
	}

}
