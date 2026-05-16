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

/**
 * Wires together all systems and holds the EntityManager.
 * Swap scenes via {@code engine.setScene()} to change game states.
 */
public class Scene {
	EntityManager entityManager;

	/** Reference to the engine, needed for runtime scene transitions. */
	Engine engine;

	InputManager inputManager;
	
	private long lastPrint = 0;
	
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
	
	/*
	public void update(double dt) {
		
		 clickSystem.update(entityManager, dt);
		playerControlSystem.update(entityManager, dt);
		//playerControlSystem.update(entityManager, dt);
		movementSystem.update(entityManager, dt);
		//clickSystem.update(entityManager, dt);
		scriptSystem.update(entityManager, dt);
		physicsSystem.update(entityManager, dt);
		pickupSystem.update(entityManager, dt);
		animationSystem.update(entityManager, dt);
		audioSystem.update(entityManager, dt);
		timeToLiveSystem.update(entityManager, dt);
	}*/
	
	// render order: tilemap -> world entities -> lighting -> UI overlay
	public void render(Graphics2D g, int screenW, int screenH) {
	    tileMapSystem.render(entityManager, g, screenW, screenH);
	    renderingSystem.renderWorld(entityManager, g, screenW, screenH);
	    lightingSystem.render(entityManager, g, screenW, screenH);
	    renderingSystem.renderUI(entityManager, g, screenW, screenH);
	}
	
	public void update(double dt) {
	    long t0 = System.nanoTime();
	    clickSystem.update(entityManager, dt);
	    long t1 = System.nanoTime();
	    playerControlSystem.update(entityManager, dt);
	    long t2 = System.nanoTime();
	    movementSystem.update(entityManager, dt);
	    long t3 = System.nanoTime();
	    scriptSystem.update(entityManager, dt);
	    long t4 = System.nanoTime();
	    physicsSystem.update(entityManager, dt);
	    long t5 = System.nanoTime();
	    pickupSystem.update(entityManager, dt);
	    long t6 = System.nanoTime();
	    animationSystem.update(entityManager, dt);
	    long t7 = System.nanoTime();
	    audioSystem.update(entityManager, dt);
	    long t8 = System.nanoTime();
	    timeToLiveSystem.update(entityManager, dt);
	    long t9 = System.nanoTime();

	    // print once a second
	    if ((t9 - lastPrint) > 1_000_000_000L) {
	        System.out.printf("click %.2f  player %.2f  move %.2f  script %.2f  physics %.2f  pickup %.2f  anim %.2f  audio %.2f  ttl %.2f ms%n",
	            (t1-t0)/1e6, (t2-t1)/1e6, (t3-t2)/1e6, (t4-t3)/1e6,
	            (t5-t4)/1e6, (t6-t5)/1e6, (t7-t6)/1e6, (t8-t7)/1e6, (t9-t8)/1e6);
	        lastPrint = t9;
	    }
	}
	
	public Scene(InputManager im, Engine engine) {
		this.inputManager = im;
		
		this.engine = engine;
		
		entityManager = new EntityManager();
		
		playerControlSystem = new PlayerControlSystem(this.inputManager,
				engine.getPixelStyle().virtualWidth, engine.getPixelStyle().virtualHeight);
		movementSystem = new MovementSystem();
		physicsSystem = new PhysicsSystem();
		pickupSystem = new PickupSystem();
		scriptSystem = new ScriptSystem();
		clickSystem = new ClickSystem(this.inputManager,
				engine.getPixelStyle().virtualWidth, engine.getPixelStyle().virtualHeight);
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

	public Entity createEntity(String name) {
		return this.entityManager.createEntity(name);
	}

	/** Adds an existing entity to this scene (e.g. a player carried across scene transitions). */
	public void addEntity(Entity e) {
		this.entityManager.addEntity(e);
	}
}
