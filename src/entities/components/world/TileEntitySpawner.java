package entities.components.world;

import core.Scene;

public interface TileEntitySpawner {
	void spawnEntity(Scene scene, int tileIndex, int worldX, int worldY);
}
