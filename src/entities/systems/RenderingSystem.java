package entities.systems;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import entities.Entity;
import entities.EntityManager;
import entities.components.rendering.FaceMouse;
import entities.components.rendering.Layer;
import entities.components.rendering.Sprite;
import entities.components.transform.Position;

public class RenderingSystem{

	public void render(EntityManager entities, Graphics2D g) {
		// TODO Auto-generated method stub
		List<Entity> MyList = entities.getEntities();
		
		MyList.sort(Comparator.comparingInt(e -> e.has(Layer.class) ? e.get(Layer.class).layerLevel : 0));
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
	}

}


