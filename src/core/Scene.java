package core;

import java.awt.Graphics2D;

import entities.Entity;
import entities.EntityManager;
import entities.systems.MovementSystem;
import entities.systems.RenderingSystem;
import entities.systems.PhysicsSystem;
import entities.systems.PickupSystem;
import entities.systems.PlayerControlSystem;
import input.InputManager;

public class Scene {
	EntityManager entityManager;
	
	InputManager inputManager;
	
	PlayerControlSystem playerControlSystem;
	MovementSystem movementSystem;
	PhysicsSystem physicsSystem;
	PickupSystem pickupSystem;

	RenderingSystem renderingSystem;
	
	public void update(double dt) {
		playerControlSystem.update(entityManager, dt);
		movementSystem.update(entityManager, dt);
		physicsSystem.update(entityManager, dt);
		pickupSystem.update(entityManager, dt);
	}
	
	public void render(Graphics2D g) {
		renderingSystem.render(entityManager, g);
	}
	
	public Scene(InputManager im) {
		this.inputManager = im;
		
		entityManager = new EntityManager();
		
		playerControlSystem = new PlayerControlSystem(this.inputManager);
		movementSystem = new MovementSystem();
		physicsSystem = new PhysicsSystem();
		pickupSystem = new PickupSystem();

		renderingSystem = new RenderingSystem();
		
	}
	
	public void addEntity(Entity e) {
		this.entityManager.addEntity(e);
	}
}
