package entities.components.world;

import java.awt.image.BufferedImage;

import entities.components.Component;

/**
 * Holds tilemap data for a 2D tile grid.
 * Loaded and rendered by TileMapSystem.
 */
public class TileMap extends Component {
	/** Pixel size of each tile. */
	public int tileSize;
	/** 2D array of tile indices. */
	public int[][] map;
	/** Map width in tiles. */
	public int mapWidth;
	/** Map height in tiles. */
	public int mapHeight;
	/** Classpath to the tileset image. */
	public String tilesetPath;
	/** Classpath to the .map file. */
	public String mapPath;

	/** The loaded tileset image. Set by TileMapSystem. */
	public BufferedImage tileset;
	/** Pre-sliced tile images. Set by TileMapSystem. */
	public BufferedImage[] tileCache;
	/** True once TileMapSystem has loaded assets. */
	public boolean loaded = false;

	public TileMap(int tileSize) {
		this.tileSize = tileSize;
	}

	public TileMap setTileset(String path) {
		this.tilesetPath = path;
		return this;
	}

	public TileMap setMap(String path) {
		this.mapPath = path;
		return this;
	}

	public BufferedImage getTileImage(int tileIndex) {
		if (tileIndex < 0 || tileCache == null || tileIndex >= tileCache.length) return null;
		return tileCache[tileIndex];
	}
}
