package entities.systems;

import entities.Entity;
import entities.EntityManager;
import entities.components.input.InputState;
import entities.components.input.PlayerControlled;
import entities.components.movement.MovementValues;
import input.InputManager;

public class TransformSystem implements GameSystem {

	InputManager im;
	
	public TransformSystem(InputManager im){
		this.im = im;
	}
	
	@Override
	public void update(EntityManager entityManager, double dt) {
		for (Entity e : entityManager.getEntities()) {
			if (!e.has(PlayerControlled.class) || !e.has(InputState.class) || !e.has(MovementValues.class)) continue;

			InputState input = e.get(InputState.class);
			
			input.movingUp = im.getKeyboard().W_key_pressed;
			input.movingDown = im.getKeyboard().S_key_pressed;
			input.movingLeft = im.getKeyboard().A_key_pressed;
			input.movingRight = im.getKeyboard().D_key_pressed;
			
			MovementValues mov = e.get(MovementValues.class);
			
			mov.velocityX = 0;
			mov.velocityY = 0;

			if (input.movingUp)    mov.velocityY -= mov.speed;
			if (input.movingDown)  mov.velocityY += mov.speed;
			if (input.movingLeft)  mov.velocityX -= mov.speed;
			if (input.movingRight) mov.velocityX += mov.speed;
		}
	}

}
