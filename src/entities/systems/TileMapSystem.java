package entities.systems;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import entities.Entity;
import entities.EntityManager;
import entities.components.rendering.Camera;
import entities.components.rendering.Sprite;
import entities.components.transform.Position;
import entities.components.world.TileMap;

public class TileMapSystem {

	public void render(EntityManager entities, Graphics2D g, int screenW, int screenH) {
		// find camera offset
		double camX = 0, camY = 0;
		double camRotation = 0;
		double camZoom = 1.0;
		for (Entity e : entities.getEntities()) {
			if (e.has(Camera.class)) {
				Camera cam = e.get(Camera.class);
				camRotation = cam.rotation;
				camZoom = cam.zoom;
				if (cam.target != null && cam.target.has(Position.class)) {
					Entity target = cam.target;
					Position tp = target.get(Position.class);
					double targetCenterX = tp.x;
					double targetCenterY = tp.y;
					if (target.has(Sprite.class) && target.get(Sprite.class).image != null) {
						targetCenterX += target.get(Sprite.class).image.getWidth() / 2.0;
						targetCenterY += target.get(Sprite.class).image.getHeight() / 2.0;
					}
					// rotate user offset so it stays relative to screen, not world
					double sin = Math.sin(cam.rotation);
					double cos = Math.cos(cam.rotation);
					double rotOffX = cam.userOffsetX * cos - cam.userOffsetY * sin;
					double rotOffY = cam.userOffsetX * sin + cam.userOffsetY * cos;
					camX = targetCenterX - screenW / 2.0 + rotOffX;
					camY = targetCenterY - screenH / 2.0 + rotOffY;

					cam.offsetX = camX;
					cam.offsetY = camY;
				}
				break;
			}
		}

		// apply camera transform: zoom + rotate around screen center, then translate
		AffineTransform baseTransform = g.getTransform();
		g.translate(screenW / 2.0, screenH / 2.0);
		g.scale(camZoom, camZoom);
		g.rotate(-camRotation);
		g.translate(-screenW / 2.0, -screenH / 2.0);
		g.translate(-camX, -camY);

		for (Entity e : entities.getEntities()) {
			if (!e.has(TileMap.class) || !e.has(Position.class)) continue;

			TileMap tm = e.get(TileMap.class);
			Position pos = e.get(Position.class);

			if (tm.map == null || tm.tileset == null) continue;

			// only draw tiles visible on screen (account for zoom)
			double visibleW = screenW / camZoom;
			double visibleH = screenH / camZoom;
			int startCol = Math.max(0, (int) ((camX - pos.x) / tm.tileSize));
			int startRow = Math.max(0, (int) ((camY - pos.y) / tm.tileSize));
			int endCol = Math.min(tm.mapWidth, (int) ((camX + visibleW - pos.x) / tm.tileSize) + 2);
			int endRow = Math.min(tm.mapHeight, (int) ((camY + visibleH - pos.y) / tm.tileSize) + 2);

			for (int row = startRow; row < endRow; row++) {
				for (int col = startCol; col < endCol; col++) {
					int tileIndex = tm.map[row][col];
					if (tileIndex < 0) continue; // -1 = empty/no tile

					BufferedImage tileImg = tm.getTileImage(tileIndex);
					if (tileImg == null) continue;

					double drawX = pos.x + col * tm.tileSize;
					double drawY = pos.y + row * tm.tileSize;
					g.translate(drawX, drawY);
					g.drawImage(tileImg, 0, 0, null);
					g.translate(-drawX, -drawY);
				}
			}
		}

		g.setTransform(baseTransform);
	}
}
