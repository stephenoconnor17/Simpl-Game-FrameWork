package core;

import java.awt.Graphics2D;

import entities.Entity;
import entities.EntityManager;
import entities.systems.MovementSystem;
import entities.systems.RenderingSystem;
import entities.systems.PhysicsSystem;
import entities.systems.PickupSystem;
import entities.systems.PlayerControlSystem;
import entities.systems.ScriptSystem;
import input.InputManager;

public class Scene {
	EntityManager entityManager;
	
	InputManager inputManager;
	
	PlayerControlSystem playerControlSystem;
	MovementSystem movementSystem;
	PhysicsSystem physicsSystem;
	PickupSystem pickupSystem;
	ScriptSystem scriptSystem;

	RenderingSystem renderingSystem;
	
	public void update(double dt) {
		playerControlSystem.update(entityManager, dt);
		movementSystem.update(entityManager, dt);
		physicsSystem.update(entityManager, dt);
		pickupSystem.update(entityManager, dt);
		scriptSystem.update(entityManager, dt);
	}
	
	public void render(Graphics2D g, int screenW, int screenH) {
		renderingSystem.render(entityManager, g, screenW, screenH);
	}
	
	public Scene(InputManager im) {
		this.inputManager = im;
		
		entityManager = new EntityManager();
		
		playerControlSystem = new PlayerControlSystem(this.inputManager);
		movementSystem = new MovementSystem();
		physicsSystem = new PhysicsSystem();
		pickupSystem = new PickupSystem();
		scriptSystem = new ScriptSystem();

		renderingSystem = new RenderingSystem();
		
	}
	
	public void addEntity(Entity e) {
		this.entityManager.addEntity(e);
	}
}
