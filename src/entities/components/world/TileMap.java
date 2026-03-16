package entities.components.world;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import entities.Entity;
import entities.EntityManager;
import entities.components.Component;

public class TileMap extends Component {
	public BufferedImage tileset;
	public int tileSize;
	public int[][] map;
	public int mapWidth;
	public int mapHeight;

	// how many tiles fit in one row of the tileset image
	private int tilesetColumns;
	private BufferedImage[] tileCache;

	public TileMap(int tileSize) {
		this.tileSize = tileSize;
	}

	public TileMap setTileset(String imageLink) {
		try {
			tileset = ImageIO.read(getClass().getClassLoader().getResource("sprites/" + imageLink));
			tilesetColumns = tileset.getWidth() / tileSize;
			int tilesetRows = tileset.getHeight() / tileSize;
			int totalTiles = tilesetColumns * tilesetRows;
			tileCache = new BufferedImage[totalTiles];
			for (int i = 0; i < totalTiles; i++) {
				int col = i % tilesetColumns;
				int row = i / tilesetColumns;
				BufferedImage tile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
				tile.getGraphics().drawImage(tileset,
						0, 0, tileSize, tileSize,
						col * tileSize, row * tileSize,
						(col + 1) * tileSize, (row + 1) * tileSize, null);
				tileCache[i] = tile;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public TileMap loadMap(String mapFile) {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(
						getClass().getClassLoader().getResourceAsStream("maps/" + mapFile)))) {
			List<int[]> rows = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) continue;
				String[] tokens = line.split("\\s+");
				int[] row = new int[tokens.length];
				for (int i = 0; i < tokens.length; i++) {
					row[i] = Integer.parseInt(tokens[i]);
				}
				rows.add(row);
			}
			mapHeight = rows.size();
			mapWidth = rows.get(0).length;
			map = rows.toArray(new int[0][]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public void spawnEntities(EntityManager entityManager, double originX, double originY, TileEntitySpawner spawner) {
		if (map == null) return;
		for (int row = 0; row < mapHeight; row++) {
			for (int col = 0; col < mapWidth; col++) {
				int tileIndex = map[row][col];
				int worldX = (int) (originX + col * tileSize);
				int worldY = (int) (originY + row * tileSize);
				Entity spawned = spawner.spawnEntity(tileIndex, worldX, worldY);
				if (spawned != null) {
					entityManager.addEntity(spawned);
				}
			}
		}
	}

	public BufferedImage getTileImage(int tileIndex) {
		if (tileIndex < 0 || tileCache == null || tileIndex >= tileCache.length) return null;
		return tileCache[tileIndex];
	}
}
