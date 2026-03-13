package entities.systems;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Comparator;
import java.util.List;

import entities.Entity;
import entities.EntityManager;
import entities.components.rendering.Camera;
import entities.components.rendering.Layer;
import entities.components.rendering.Sprite;
import entities.components.transform.Position;

public class RenderingSystem{

	public void render(EntityManager entities, Graphics2D g, int screenW, int screenH) {
		List<Entity> MyList = entities.getEntities();

		MyList.sort(Comparator.comparingInt(e -> e.has(Layer.class) ? e.get(Layer.class).layerLevel : 0));

		// find camera offset
		double camX = 0, camY = 0;

		for (Entity e : MyList) {
			if (e.has(Camera.class)) {
				Camera cam = e.get(Camera.class);
				if (cam.target != null && cam.target.has(Position.class)) {
					Entity target = cam.target;
					Position tp = target.get(Position.class);
					double targetCenterX = tp.x;
					double targetCenterY = tp.y;
					if (target.has(Sprite.class) && target.get(Sprite.class).image != null) {
						targetCenterX += target.get(Sprite.class).image.getWidth() / 2.0;
						targetCenterY += target.get(Sprite.class).image.getHeight() / 2.0;
					}
					camX = targetCenterX - screenW / 2.0 + cam.userOffsetX;
					camY = targetCenterY - screenH / 2.0 + cam.userOffsetY;

					cam.offsetX = camX;
					cam.offsetY = camY;
				}
				break;
			}
		}

		// apply camera translation
		AffineTransform baseTransform = g.getTransform();
		g.translate(-camX, -camY);

		for(Entity e : MyList) {
			if(!e.has(Sprite.class) || !e.has(Position.class))continue;

			Position pos = e.get(Position.class);
			Sprite spr = e.get(Sprite.class);

			AffineTransform old = g.getTransform();

			double centerX = pos.x + spr.image.getWidth() / 2.0;
			double centerY = pos.y + spr.image.getHeight() / 2.0;

			g.rotate(pos.rotation, centerX, centerY);
			g.drawImage(spr.image, (int)pos.x, (int)pos.y, null);

			g.setTransform(old);
		}

		g.setTransform(baseTransform);
	}

}
