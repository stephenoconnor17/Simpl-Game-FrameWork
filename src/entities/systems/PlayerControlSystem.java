package entities.systems;

import entities.Entity;
import entities.EntityManager;
import entities.components.input.InputState;
import entities.components.input.PlayerControlled;
import entities.components.movement.MovementValues;
import entities.components.rendering.FaceMouse;
import entities.components.rendering.Sprite;
import entities.components.transform.Position;
import input.InputManager;

public class PlayerControlSystem implements GameSystem {

	InputManager im;

	public PlayerControlSystem(InputManager im) {
		this.im = im;
	}

	@Override
	public void update(EntityManager entityManager, double dt) {
		for (Entity e : entityManager.getEntities()) {
			if (e.has(PlayerControlled.class) && e.has(InputState.class) && e.has(MovementValues.class)
					&& e.has(Position.class)) {

				InputState input = e.get(InputState.class);

				input.movingUp = im.getKeyboard().W_key_pressed;
				input.movingDown = im.getKeyboard().S_key_pressed;
				input.movingLeft = im.getKeyboard().A_key_pressed;
				input.movingRight = im.getKeyboard().D_key_pressed;

				input.mouseX = im.getMouse().x;
				input.mouseY = im.getMouse().y;

				MovementValues mov = e.get(MovementValues.class);
				Position pos = e.get(Position.class);
				
				mov.velocityX = 0;
				mov.velocityY = 0;

				if (input.movingUp)
					mov.velocityY -= mov.speed;
				if (input.movingDown)
					mov.velocityY += mov.speed;
				if (input.movingLeft)
					mov.velocityX -= mov.speed;
				if (input.movingRight)
					mov.velocityX += mov.speed;
			}
			
			if(e.has(FaceMouse.class)) {
				MovementValues mov = e.get(MovementValues.class);
				Position pos = e.get(Position.class);
				
				double centerX = pos.x;
				double centerY = pos.y;
				if (e.has(Sprite.class) && e.get(Sprite.class).image != null) {
					centerX += e.get(Sprite.class).image.getWidth() / 2.0;
					centerY += e.get(Sprite.class).image.getHeight() / 2.0;
				}
				
				if(e.has(FaceMouse.class)) 
					pos.rotation = -Math.atan2(im.getMouse().x - centerX, im.getMouse().y - centerY);

			}
		}

	}

}
