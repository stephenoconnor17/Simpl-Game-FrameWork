package core;

import entities.Entity;
import entities.components.*;
import entities.components.input.InputState;
import entities.components.input.PlayerControlled;
import entities.components.movement.MovementValues;
import entities.components.physics.Collision;
import entities.components.physics.Pickup;
import entities.components.rendering.FaceMouse;
import entities.components.rendering.Layer;
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
		//this method is pure testing.
		//this is hopefully how quick scenes of games can be iterated on.
		Scene scene = new Scene(im);
		
		Entity player = new Entity(0,"player");
		player.add(new Position());
		player.add(new MovementValues());
		player.add(new InputState());
		player.add(new PlayerControlled());
		player.add(new Collision());
		player.add(new FaceMouse());
		player.add(new Layer().setLayerLevel(1));
		player.add(new Sprite().setImageLink("bluesquare.png"));
		
		Entity enemy = new Entity(0,"player");
		enemy.add(new Position());
		enemy.add(new Sprite().setImageLink("bluesquare.png"));
		enemy.add(new Layer().setLayerLevel(0));
		Collision col = new Collision();
		enemy.add(col);
		enemy.add(new FaceMouse());
		enemy.get(Position.class).x = 200;
		enemy.get(Position.class).y = 200;
		
		// coin pickup - has Collision but no RigidBody, so no push-apart
		Entity coin = new Entity(0, "coin");
		coin.add(new Position());
		coin.add(new Sprite().setImageLink("bluesquare.png"));
		coin.add(new Layer().setLayerLevel(0));
		Collision coinCol = new Collision();
		coinCol.solid = false;  // no physics response
		coin.add(coinCol);
		coin.add(new Pickup());
		coin.get(Position.class).x = 400;
		coin.get(Position.class).y = 300;

		scene.addEntity(player);
		scene.addEntity(enemy);
		scene.addEntity(coin);

		return scene;
	}
}
