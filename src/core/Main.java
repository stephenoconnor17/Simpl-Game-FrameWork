package core;

import entities.Entity;
import entities.components.*;
import entities.components.input.InputState;
import entities.components.input.PlayerControlled;
import entities.components.movement.MovementValues;
import entities.components.rendering.Sprite;
import entities.components.transform.Position;
import input.InputManager;

public class Main {
	public static void main(String[] args) {
		Window w = new Window("Hello");
		Engine e = new Engine(w.getRenderSurface());
		//set scene here.
		e.setScene(myScene(e.getInputManager()));
		//
		w.setEngine(e);

		e.start();
	}
	
	public static Scene myScene(InputManager im) {
		Scene scene = new Scene(im);
		
		Entity player = new Entity(0,"player");
		player.add(new Position());
		player.add(new MovementValues());
		player.add(new InputState());
		player.add(new PlayerControlled());
		player.add(new Sprite().setImageLink("bluesquare.png"));
		
		scene.addEntity(player);
		
		return scene;
	}
}
