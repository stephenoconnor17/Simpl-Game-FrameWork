package core;

import java.awt.Graphics2D;

import javax.swing.JFrame;

import entities.Entity;
import entities.EntityManager;
import entities.systems.MovementSystem;
import entities.systems.RenderingSystem;
import entities.systems.PhysicsSystem;
import entities.systems.PickupSystem;
import entities.systems.PlayerControlSystem;
import entities.systems.ScriptSystem;
import entities.systems.TileMapSystem;
import entities.systems.LightingSystem;
import entities.systems.AnimationSystem;
import entities.systems.AudioSystem;
import entities.systems.ClickSystem;
import entities.systems.TimeToLiveSystem;
import input.InputManager;

public class Scene {
	EntityManager entityManager;
	
	InputManager inputManager;
	
	PlayerControlSystem playerControlSystem;
	MovementSystem movementSystem;
	PhysicsSystem physicsSystem;
	PickupSystem pickupSystem;
	ScriptSystem scriptSystem;
	ClickSystem clickSystem;
	AnimationSystem animationSystem;
	AudioSystem audioSystem;
	TimeToLiveSystem timeToLiveSystem;

	TileMapSystem tileMapSystem;
	RenderingSystem renderingSystem;
	LightingSystem lightingSystem;
	
	public void update(double dt) {
		playerControlSystem.update(entityManager, dt);
		movementSystem.update(entityManager, dt);
		clickSystem.update(entityManager, dt);
		scriptSystem.update(entityManager, dt);
		physicsSystem.update(entityManager, dt);
		pickupSystem.update(entityManager, dt);
		animationSystem.update(entityManager, dt);
		audioSystem.update(entityManager, dt);
		timeToLiveSystem.update(entityManager, dt);
	}
	
	public void render(Graphics2D g, int screenW, int screenH) {
		tileMapSystem.render(entityManager, g, screenW, screenH);
		renderingSystem.render(entityManager, g, screenW, screenH);
		lightingSystem.render(entityManager, g, screenW, screenH);
	}
	
	public Scene(InputManager im) {
		this.inputManager = im;
		
		entityManager = new EntityManager();
		
		playerControlSystem = new PlayerControlSystem(this.inputManager);
		movementSystem = new MovementSystem();
		physicsSystem = new PhysicsSystem();
		pickupSystem = new PickupSystem();
		scriptSystem = new ScriptSystem();
		clickSystem = new ClickSystem(this.inputManager);
		animationSystem = new AnimationSystem();
		audioSystem = new AudioSystem();
		timeToLiveSystem = new TimeToLiveSystem();
		tileMapSystem = new TileMapSystem();
		renderingSystem = new RenderingSystem();
		lightingSystem = new LightingSystem();
		
	}
	
	public LightingSystem getLightingSystem() {
		return lightingSystem;
	}

	public void addEntity(Entity e) {
		this.entityManager.addEntity(e);
	}
}
