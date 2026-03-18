package entities.systems;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import entities.EntityManager;
import entities.components.input.PlayerControlled;
import entities.components.physics.Collision;
import entities.components.physics.Pickup;
import entities.components.util.TimeToLive;

public class TimeToLiveSystem implements GameSystem{

	@Override
	public void update(EntityManager entityManager, double dt) {
		List<Entity> toRemove = new ArrayList<>();

		for (Entity e : entityManager.getEntities()) {
			//only check if its a player with collision
			if (!e.has(TimeToLive.class)) continue;
			
			TimeToLive ttl = e.get(TimeToLive.class);
			ttl.ttl -= dt; //when delta time adds every frame at 60fps, it should then equal one second! this is true for literally every fps.
			if(ttl.ttl <= 0) {
				toRemove.add(e);
			}
		}

		for (Entity e : toRemove) {
			entityManager.removeEntity(e);
		}
	}

}
