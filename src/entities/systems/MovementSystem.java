package entities.systems;

import entities.Entity;
import entities.EntityManager;
import entities.components.movement.MovementValues;
import entities.components.transform.ChildOf;
import entities.components.transform.Position;

/**
 * Applies velocity to position each frame and syncs child entities
 * to their parent's position (plus offset).
 */
public class MovementSystem implements GameSystem {

	@Override
	public void update(EntityManager entityManager, double dt) {
		for (Entity e : entityManager.getEntities()) {
			// apply velocity to position
			if (e.has(Position.class) && e.has(MovementValues.class)) {
				Position pos = e.get(Position.class);
				MovementValues mov = e.get(MovementValues.class);

				pos.x += mov.velocityX * dt;
				pos.y += mov.velocityY * dt;
				pos.markDirty();
			}

			// sync child position to parent + offset
			if(e.has(Position.class) && e.has(ChildOf.class)) {
				ChildOf childLink = e.get(ChildOf.class);
				Entity parent = childLink.parentEntity;
				if(parent  == null) {
					continue;
				}

				if(parent.has(Position.class)) {
					Position parentPos = parent.get(Position.class);
					if(parentPos == null) {
						continue;
					}

					Position pos = e.get(Position.class);
					pos.x = parentPos.x + childLink.offsetX;
					pos.y = parentPos.y + childLink.offsetY;
				}
			}
		}
	}

}
