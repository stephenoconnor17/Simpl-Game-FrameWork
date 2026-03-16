package entities.components.world;

import entities.Entity;

public interface TileEntitySpawner {
	Entity spawnEntity(int tileIndex, int worldX, int worldY);
}
