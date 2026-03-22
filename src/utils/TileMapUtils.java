package utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import entities.Entity;
import entities.EntityManager;
import entities.components.world.TileEntitySpawner;
import entities.components.world.TileMap;

public class TileMapUtils {

	public static void loadMap(TileMap tm) {
		if (tm.mapPath != null && tm.map == null) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(
							TileMapUtils.class.getClassLoader().getResourceAsStream("maps/" + tm.mapPath)))) {
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
				tm.mapHeight = rows.size();
				tm.mapWidth = rows.get(0).length;
				tm.map = rows.toArray(new int[0][]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadTileset(TileMap tm) {
		if (tm.tilesetPath != null && tm.tileset == null) {
			try {
				tm.tileset = ImageIO.read(TileMapUtils.class.getClassLoader().getResource("sprites/" + tm.tilesetPath));
				int tilesetColumns = tm.tileset.getWidth() / tm.tileSize;
				int tilesetRows = tm.tileset.getHeight() / tm.tileSize;
				int totalTiles = tilesetColumns * tilesetRows;
				tm.tileCache = new BufferedImage[totalTiles];
				for (int i = 0; i < totalTiles; i++) {
					int col = i % tilesetColumns;
					int row = i / tilesetColumns;
					BufferedImage tile = new BufferedImage(tm.tileSize, tm.tileSize, BufferedImage.TYPE_INT_ARGB);
					tile.getGraphics().drawImage(tm.tileset,
							0, 0, tm.tileSize, tm.tileSize,
							col * tm.tileSize, row * tm.tileSize,
							(col + 1) * tm.tileSize, (row + 1) * tm.tileSize, null);
					tm.tileCache[i] = tile;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void load(TileMap tm) {
		loadTileset(tm);
		loadMap(tm);
		tm.loaded = true;
	}

	public static void spawnEntities(TileMap tm, EntityManager em,
			double originX, double originY, TileEntitySpawner spawner) {
		if (tm.map == null) loadMap(tm);
		for (int row = 0; row < tm.mapHeight; row++) {
			for (int col = 0; col < tm.mapWidth; col++) {
				int tileIndex = tm.map[row][col];
				int worldX = (int) (originX + col * tm.tileSize);
				int worldY = (int) (originY + row * tm.tileSize);
				Entity spawned = spawner.spawnEntity(tileIndex, worldX, worldY);
				if (spawned != null) {
					em.addEntity(spawned);
				}
			}
		}
	}
}
