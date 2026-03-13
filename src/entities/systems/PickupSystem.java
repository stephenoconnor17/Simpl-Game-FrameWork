package entities.systems;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import entities.EntityManager;
import entities.components.input.PlayerControlled;
import entities.components.physics.Collision;
import entities.components.physics.Pickup;

public class PickupSystem implements GameSystem {

	@Override
	public void update(EntityManager entityManager, double dt) {
		List<Entity> toRemove = new ArrayList<>();

		for (Entity e : entityManager.getEntities()) {
			//only check if its a player with collision
			if (!e.has(Collision.class) && !e.has(PlayerControlled.class)) continue;

			Collision col = e.get(Collision.class);

			for (Entity other : col.collidedWith) {
				if (other.has(Pickup.class)) {
					Pickup pickup = other.get(Pickup.class);
					if (!pickup.collected) {
						pickup.collected = true;
						System.out.println(e.getEntityName() + " picked up " + pickup.type);
						toRemove.add(other);
					}
				}
			}
		}

		for (Entity e : toRemove) {
			entityManager.removeEntity(e);
		}
	}
}
