package entities.systems;

import entities.Entity;
import entities.EntityManager;
import entities.components.input.InputState;
import entities.components.input.PlayerControlled;
import entities.components.movement.MovementValues;
import entities.components.rendering.Camera;
import entities.components.rendering.FaceEntity;
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
		// find camera offset for screen-to-world conversion
		double camOffX = 0, camOffY = 0;
		for (Entity ce : entityManager.getEntities()) {
			if (ce.has(Camera.class)) {
				Camera cam = ce.get(Camera.class);
				camOffX = cam.offsetX;
				camOffY = cam.offsetY;
				break;
			}
		}

		for (Entity e : entityManager.getEntities()) {
			if (e.has(PlayerControlled.class) && e.has(InputState.class) && e.has(MovementValues.class)
					&& e.has(Position.class)) {

				InputState input = e.get(InputState.class);

				input.movingUp = im.getKeyboard().W_key_pressed;
				input.movingDown = im.getKeyboard().S_key_pressed;
				input.movingLeft = im.getKeyboard().A_key_pressed;
				input.movingRight = im.getKeyboard().D_key_pressed;

				input.mouseX = (int)(im.getMouse().x + camOffX);
				input.mouseY = (int)(im.getMouse().y + camOffY);

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

			if (e.has(FaceMouse.class)) {
				FaceMouse efm = e.get(FaceMouse.class);

				if (!efm.faceingMouse)
					continue;

				Position pos = e.get(Position.class);

				double centerX = pos.x;
				double centerY = pos.y;
				if (e.has(Sprite.class) && e.get(Sprite.class).image != null) {
					centerX += e.get(Sprite.class).image.getWidth() / 2.0;
					centerY += e.get(Sprite.class).image.getHeight() / 2.0;
				}

				double worldMouseX = im.getMouse().x + camOffX;
				double worldMouseY = im.getMouse().y + camOffY;
				pos.rotation = -Math.atan2(worldMouseX - centerX, worldMouseY - centerY);

			}

			if (e.has(FaceEntity.class)) {
				Position pos = e.get(Position.class);
				FaceEntity efe = e.get(FaceEntity.class);
				// face entity is turned off.
				if (!efe.faceEntity)
					continue;

				Entity otherEntity = e.get(FaceEntity.class).getEntityToFace();

				if (otherEntity == null)
					continue;

				Position oPos = otherEntity.get(Position.class);

				double eCenterX = pos.x;
				double eCenterY = pos.y;

				double oeCenterX = oPos.x;
				double oeCenterY = oPos.y;

				if (e.has(Sprite.class) && e.get(Sprite.class).image != null) {
					eCenterX += e.get(Sprite.class).image.getWidth() / 2.0;
					eCenterY += e.get(Sprite.class).image.getHeight() / 2.0;
				}

				if (otherEntity.has(Sprite.class) && otherEntity.get(Sprite.class).image != null) {
					oeCenterX += e.get(Sprite.class).image.getWidth() / 2.0;
					oeCenterY += e.get(Sprite.class).image.getHeight() / 2.0;
				}

				pos.rotation = -Math.atan2(oeCenterX - eCenterX, oeCenterY - eCenterY);
			}
		}

	}

}
