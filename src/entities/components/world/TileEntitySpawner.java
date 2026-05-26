package entities.components.world;

import core.Scene;

/** Callback for spawning entities from tile indices during tilemap loading. */
public interface TileEntitySpawner {
	void spawnEntity(Scene scene, int tileIndex, int worldX, int worldY);
}
