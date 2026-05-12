package core;

import entities.Entity;
import entities.EntityManager;
import entities.components.*;
import entities.components.input.InputState;
import entities.components.input.PlayerControlled;
import entities.components.movement.MovementValues;
import entities.components.physics.Collision;
import entities.components.physics.Pickup;
import entities.components.physics.RigidBody;
import entities.components.rendering.Animation;
import entities.components.rendering.Camera;
import entities.components.rendering.FaceEntity;
import entities.components.rendering.FaceMouse;
import entities.components.rendering.Layer;
import entities.components.rendering.Light;
import entities.components.rendering.RotateViewToMouse;
import entities.components.rendering.Sprite;
import entities.components.rendering.UIElement;
import entities.components.transform.ChildOf;
import entities.components.transform.Position;
import entities.components.audio.AudioSource;
import entities.components.util.TimeToLive;
import entities.components.world.TileMap;
import input.InputManager;
import utils.TileMapUtils;

import entities.components.Creator.*;

public class Main {
	public static void main(String[] args) {
		
		Window w = new Window("Hello");
		Engine e = new Engine(w.getRenderSurface(), PixelStyle.BIT_8);
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
		
		Entity player = scene.createEntity("player");
		
		
		/*
		player.add(new Position());
		player.add(new MovementValues());
		player.add(new InputState().setKeyboardToMove(true)); 
		player.add(new PlayerControlled());
		player.add(new Collision());
		player.add(new RigidBody());
		player.add(new FaceMouse());
		player.add(new Layer().setLayerLevel(1));
		player.add(new Sprite().setImageLink("blue8bitsqr.png"));
		player.add(new Light().setRadius(18).setIntensity(0.5));
		*/
		
		//new static creator way of adding components (adds faster iteration by showing possible components.)
		player.add(Creator.position());
		player.add(Creator.movementValues());
		player.add(Creator.inputState().setKeyboardToMove(true));
		player.add(Creator.playerControlled());
		player.add(Creator.collision());
		player.add(Creator.rigidBody());
		player.add(Creator.faceMouse());
		player.add(Creator.layer().setLayerLevel(1));
		player.add(Creator.sprite()); //for animation there needs to be a sprite component.
		//as the animation module changes the sprite module which is the logical location of sprites.
		player.add(Creator.animation()
				.addAnimation("idle",       "idle-animation-test.png",       3, 8, 8, 0.25, true)
			    .setCurrentAnimation("idle"));
		
		/*  .addAnimation("walk_down",  "player_walk_down.png",  4, 32, 32, 0.12, true)
	    .addAnimation("walk_up",    "player_walk_up.png",    4, 32, 32, 0.12, true)
	    .addAnimation("walk_left",  "player_walk_left.png",  4, 32, 32, 0.12, true)
	    .addAnimation("walk_right", "player_walk_right.png", 4, 32, 32, 0.12, true)
	    */
		
		  player.add(new ScriptComponent((self, em, dt) -> {
		      InputState in = self.get(InputState.class);
		      Animation anim = self.get(Animation.class);

		      if      (in.movingUp)    anim.setCurrentAnimation("walk_up");
		      else if (in.movingDown)  anim.setCurrentAnimation("walk_down");
		      else if (in.movingLeft)  anim.setCurrentAnimation("walk_left");
		      else if (in.movingRight) anim.setCurrentAnimation("walk_right");
		      else anim.setCurrentAnimation("idle");
		  }));


		player.add(Creator.light().setRadius(18).setIntensity(1.0));
		
		//Entity view = scene.createEntity("view");
		Entity camera = scene.createEntity("camera");
		Camera cam = new Camera().setTarget(player);
		//cam.zoom = 1.5;
		camera.add(cam);
		camera.add(new RotateViewToMouse());
		cam.userOffsetY = -20;
		
		Entity enemy = scene.createEntity("enemy");
		enemy.add(new Position());
		enemy.add(new RigidBody());
		enemy.add(new Sprite().setImageLink("blue8bitsqr.png"));
		enemy.add(new Layer().setLayerLevel(0));
		
		Entity enemyBorder = scene.createEntity("enemyBorder");
		enemyBorder.add(new ChildOf().setParentEntity(enemy));
		enemyBorder.add(new Position());
		enemyBorder.add(new AudioSource().setFilePath("beep.wav").setVolume(0.5f));
		Collision newCol = new Collision();
		newCol.shape = Collision.Shape.CIRCLE;
		newCol.radius = 16;
		enemyBorder.add(newCol);
		enemyBorder.add(new ScriptComponent((self, entityManager, dt) -> {
				if(self.has(Position.class) && self.has(ChildOf.class) && self.has(Collision.class)) {

					Position thisP = self.get(Position.class);
					Entity pe = self.get(ChildOf.class).parentEntity;
					Position pePos = pe.get(Position.class);
					thisP.x = pePos.x;
					thisP.y = pePos.y;
					if (pe.has(Sprite.class) && pe.get(Sprite.class).image != null) {
						thisP.x += pe.get(Sprite.class).image.getWidth() / 2.0;
						thisP.y += pe.get(Sprite.class).image.getHeight() / 2.0;
					}

					Collision col = self.get(Collision.class);
					Entity playerEntity = entityManager.getEntity("player");
					// Play sound when player enters the border (only triggers once per contact)
					AudioSource audio = self.get(AudioSource.class);
					if(col.collidedWith.contains(playerEntity)) {
						if (!audio.playing) { 
							audio.play = true;
						}
						pe.get(Position.class).y += 1;
					}//This if statement is a bad example of it because it fires everytime collision is detected from a moving enemy. behaviour needs proper user definition.
				}
		}));
		
		Collision col = new Collision();
		enemy.add(col);
		//enemy.add(new FaceEntity(player));
		enemy.get(Position.class).x = 20;
		enemy.get(Position.class).y = 20;
		// coin pickup - has Collision but no RigidBody, so no push-apart
		Entity coin = scene.createEntity("coin");
		coin.add(new Position());
		coin.add(new Sprite().setImageLink("blue8bitsqr.png"));
		coin.add(new Layer().setLayerLevel(0));
		coin.add(new TimeToLive().setTTL(10));
		Collision coinCol = new Collision();
		coinCol.solid = false;  // no physics response
		coinCol.radius = 4;
		coin.add(coinCol);
		coin.add(new Pickup());
		coin.get(Position.class).x = 20;
		coin.get(Position.class).y = 20;
			
		Entity lightSource = scene.createEntity("light source");
		lightSource.add(new Light().setIntensity(1.0).setRadius(24));
		Position p2 = new Position();
		p2.x = 30;
		p2.y = 30;
		lightSource.add(p2);
		
		
		// tilemap
		Entity mapEntity = scene.createEntity("tilemap");
		mapEntity.add(new Position());
		mapEntity.get(Position.class).x -= 32; // moving the mapEntity moves the maps.
		mapEntity.add(new TileMap(8).setTileset("MYtileset.png").setMap("test.map"));

		// spawn wall entities where tile index == 1
		//the same offset must be applied to the spawned Entites!
		//the -32 here is based on the mapEntityes x and y. they need to sync.
		TileMapUtils.spawnEntities(mapEntity.get(TileMap.class), scene, -32, 0, (sc, tileIndex, wx, wy) -> {
			if (tileIndex == 1) {
				Entity wall = sc.createEntity("wall");
				wall.add(new Position());
				wall.get(Position.class).x = wx;
				wall.get(Position.class).y = wy;
				wall.add(new Collision());
				wall.add(new RigidBody().setMovable(false));
			}
		});
		
		Entity menu = scene.createEntity("menu");
		menu.add(Creator.sprite().setImageLink("menutest.png"));  // 32x8 image
		menu.add(new UIElement()
		    .setScreenSpace(true)
		    .setAnchorX(0.5f)
		    .setAnchorY(0.5f));

		menu.add(new ScriptComponent((self, em, dt) -> {
		    Sprite spr = self.get(Sprite.class);
		    if (spr != null && spr.image != null) {
		        System.out.println("menu sprite: " + spr.image.getWidth() + " x " + spr.image.getHeight());
		    } else {
		        System.out.println("menu sprite: image not loaded yet");
		    }
		}));
		//This runs every frame and prints the actual loaded dimensions. Let it run for a second, then tell me what it prints.
		
		// lighting: toggle on/off, set ambient darkness 0-255
		scene.getLightingSystem().setEnabled(false);
		scene.getLightingSystem().setAmbientDarkness(255);


		return scene;
	}
}
