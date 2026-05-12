package core;

import entities.Entity;

/**
 * Holds persistent game state that survives scene transitions.
 * Pass the same Data instance to every scene builder so entities
 * like the player can be reused across scenes without rebuilding.
 */
public class Data {
	/** The player entity, created once and carried between scenes. */
	public Entity player;
}