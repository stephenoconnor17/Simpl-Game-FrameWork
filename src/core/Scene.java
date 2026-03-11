package core;

import java.awt.Graphics2D;

import entities.Entity;
import entities.EntityManager;
import entities.systems.MovementSystem;
import entities.systems.RenderingSystem;
import entities.systems.TransformSystem;
import input.InputManager;

public class Scene {
	EntityManager entityManager;
	
	InputManager inputManager;
	
	TransformSystem transformSystem;
	MovementSystem movementSystem;
	
	RenderingSystem renderingSystem;
	
	public void update(double dt) {
		transformSystem.update(entityManager, dt);
		movementSystem.update(entityManager, dt);
	}
	
	public void render(Graphics2D g) {
		renderingSystem.render(entityManager, g);
	}
	
	public Scene(InputManager im) {
		this.inputManager = im;
		
		entityManager = new EntityManager();
		
		transformSystem = new TransformSystem(this.inputManager);
		movementSystem = new MovementSystem();
		
		renderingSystem = new RenderingSystem();
		
	}
	
	public void addEntity(Entity e) {
		this.entityManager.addEntity(e);
	}
}
