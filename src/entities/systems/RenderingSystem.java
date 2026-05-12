package entities.systems;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import entities.Entity;
import entities.EntityManager;
import entities.components.rendering.Camera;
import entities.components.rendering.Layer;
import entities.components.rendering.Sprite;
import entities.components.rendering.UIElement;
import entities.components.transform.ChildOf;
import entities.components.transform.Position;

public class RenderingSystem{

	public void render(EntityManager entities, Graphics2D g, int screenW, int screenH) {
		List<Entity> MyList = entities.getEntities();

		MyList.sort(Comparator.comparingInt(e -> e.has(Layer.class) ? e.get(Layer.class).layerLevel : 0));

		// find camera offset
		double camX = 0, camY = 0;
		double camRotation = 0;

		for (Entity e : MyList) {
			if (e.has(Camera.class)) {
				Camera cam = e.get(Camera.class);
				camRotation = cam.rotation;
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
				cam.markDirty();
				break;
			}
		}

		double camZoom = 1.0;
		for (Entity e : MyList) {
			if (e.has(Camera.class)) { camZoom = e.get(Camera.class).zoom; break; }
		}

		// split into world entities and screen-space UI entities
		List<Entity> worldEntities = new ArrayList<>();
		List<Entity> screenUIEntities = new ArrayList<>();

		for (Entity e : MyList) {
			if (!e.has(Sprite.class)) continue;

			if (isScreenSpace(e)) {
				screenUIEntities.add(e);
			} else if (e.has(Position.class)) {
				worldEntities.add(e);
			}
		}

		// render world entities with camera transform
		AffineTransform baseTransform = g.getTransform();
		g.translate(screenW / 2.0, screenH / 2.0);
		g.scale(camZoom, camZoom);
		g.rotate(-camRotation);
		g.translate(-screenW / 2.0, -screenH / 2.0);
		g.translate(-camX, -camY);

		for(Entity e : worldEntities) {
			Position pos = e.get(Position.class);
			Sprite spr = e.get(Sprite.class);

			AffineTransform old = g.getTransform();

			double centerX = pos.x + spr.image.getWidth() / 2.0;
			double centerY = pos.y + spr.image.getHeight() / 2.0;

			g.rotate(pos.rotation, centerX, centerY);
			g.translate(pos.x, pos.y);
			g.drawImage(spr.image, 0, 0, null);
			g.translate(-pos.x, -pos.y);

			g.setTransform(old);
		}

		g.setTransform(baseTransform);

		// render screen-space UI entities using anchor percentages
		for(Entity e : screenUIEntities) {
			Sprite spr = e.get(Sprite.class);
			UIElement ui = e.has(UIElement.class) ? e.get(UIElement.class) : null;

			double drawX = ui != null ? ui.anchorX * screenW : 0;
			double drawY = ui != null ? ui.anchorY * screenH : 0;

			double rotation = e.has(Position.class) ? e.get(Position.class).rotation : 0;

			AffineTransform old = g.getTransform();

			double centerX = drawX + spr.image.getWidth() / 2.0;
			double centerY = drawY + spr.image.getHeight() / 2.0;

			g.rotate(rotation, centerX, centerY);
			g.translate(drawX, drawY);
			g.drawImage(spr.image, 0, 0, null);
			g.translate(-drawX, -drawY);

			g.setTransform(old);
		}
	}

	/**
	 * Determines if an entity should render in screen space.
	 * Checks the entity's own UIElement first, then walks up the parent
	 * chain to inherit screenSpace from the root UI entity.
	 */
	private boolean isScreenSpace(Entity e) {
		if (e.has(UIElement.class)) {
			return e.get(UIElement.class).screenSpace;
		}

		// walk up parent chain to inherit
		Entity current = e;
		while (current.has(ChildOf.class)) {
			Entity parent = current.get(ChildOf.class).parentEntity;
			if (parent == null) break;
			if (parent.has(UIElement.class)) {
				return parent.get(UIElement.class).screenSpace;
			}
			current = parent;
		}

		return false;
	}

}
