package entities.systems;

import java.awt.Graphics2D;

import entities.Entity;
import entities.EntityManager;
import entities.components.rendering.Sprite;
import entities.components.transform.Position;

public class RenderingSystem{

	public void render(EntityManager entities, Graphics2D g) {
		// TODO Auto-generated method stub
		for(Entity e : entities.getEntities()) {
			if(!e.has(Sprite.class))continue;
			
			Position pos = e.get(Position.class);
			Sprite spr = e.get(Sprite.class);
			g.drawImage(spr.image, (int)pos.x, (int)pos.y, null);
		}
	}

}
