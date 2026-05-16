package core;

import java.awt.Color;
import java.awt.Font;

import entities.Entity;
import entities.EntityManager;
import entities.components.*;
import entities.components.input.Clickable;
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
import entities.components.rendering.Text;
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

		Data data = new Data();
		e.setScene(myScene(e.getInputManager(), e, data));

		w.setEngine(e);
		e.start();
	}
	
	public static Scene myScene(InputManager im, Engine e, Data data) {
		Scene scene = new Scene(im, e);
		
		
		Entity fpsCounter = scene.createEntity("fps");
		fpsCounter.add(Creator.text()
		    .setText("fps: --")
		    .setFont(new Font("Consolas", Font.PLAIN, 16))
		    .setColour(Color.yellow));
		fpsCounter.add(Creator.uiElement()
		    .setScreenSpace(true)
		    .setAnchorX(0.0)
		    .setAnchorY(0.05));
		fpsCounter.add(new ScriptComponent(new Script() {
		    long lastTime = System.nanoTime();
		    int frames = 0;
		    int fps = 0;

		    @Override
		    public void update(Entity self, EntityManager em, double dt) {
		        frames++;
		        long now = System.nanoTime();
		        if (now - lastTime >= 1_000_000_000L) {
		            fps = frames;
		            frames = 0;
		            lastTime = now;
		        }
		        self.get(Text.class).setText("fps: " + fps);
		    }
		}));
		// first time entering — build the player, otherwise reuse the one in data
		if (data.player == null) {
			data.player = buildPlayer(scene);
		} else {
			scene.addEntity(data.player);
		}
		Entity player = data.player;
		// reset position and click-to-move target for this scene's spawn point
		player.get(Position.class).x = 0;
		player.get(Position.class).y = 0;
		player.get(InputState.class).isMovingToTarget = false;

		/*
		Entity camera = scene.createEntity("camera");
		Camera cam = new Camera().setTarget(player);
		camera.add(cam);
		cam.userOffsetY = -20;
		cam.zoom = 1.5;
		*/
		
		// enemy with a child border entity that detects player collision
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
					AudioSource audio = self.get(AudioSource.class);
					if(col.collidedWith.contains(playerEntity)) {
						if (!audio.playing) { 
							audio.play = true;
						}
						pe.get(Position.class).y += 1;
					}
				}
		}));
		
		Collision col = new Collision();
		enemy.add(col);
		enemy.get(Position.class).x = 20;
		enemy.get(Position.class).y = 20;

		// coin pickup — has Collision but no RigidBody, so no push-apart
		Entity coin = scene.createEntity("coin");
		coin.add(new Position());
		coin.add(new Sprite().setImageLink("blue8bitsqr.png"));
		coin.add(new Layer().setLayerLevel(0));
		coin.add(new TimeToLive().setTTL(10));
		Collision coinCol = new Collision();
		coinCol.solid = false;
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
		//mapEntity.get(Position.class).x -= 32;
		mapEntity.add(new TileMap(8).setTileset("MYtileset.png").setMap("test.map"));

		TileMapUtils.spawnEntities(mapEntity.get(TileMap.class), scene, 0, 0, (sc, tileIndex, wx, wy) -> {
			if (tileIndex == 1) {
				Entity wall = sc.createEntity("wall");
				wall.add(new Position());
				wall.get(Position.class).x = wx;
				wall.get(Position.class).y = wy;
				wall.add(new Collision());
				wall.add(new RigidBody().setMovable(false));
			}
		});
		
		// centered screen-space menu sprite using anchor positioning
		Entity menu = scene.createEntity("menu");
		menu.add(Creator.sprite().setImageLink("menutest.png"));
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
		
		// clickable text that shows current time and changes colour on hover/press
		Entity textTest = scene.createEntity("text");
		textTest.add(Creator.text().setText("").setFont(new Font("Consolas", Font.PLAIN, 16)).setColour(Color.white));
		textTest.add(Creator.uiElement().setAnchorX(0.0).setScreenSpace(true).setAnchorY(0.0));
		textTest.add(Creator.clickable().setEnabled(true).setBounds(120, 20));
		textTest.add(new ScriptComponent((self, em, dt) -> {
			Text t = self.get(Text.class);
			Clickable c = self.get(Clickable.class);
			
			t.setText(String.valueOf(System.currentTimeMillis()));
			
			if(c.pressed) {
				t.colour = Color.blue;
			}else if(c.hovered) {
				t.colour = Color.green;
			}else {
				t.colour = Color.white;
			}
		}));
		
		// scene transition button — click to enter the house
		Entity goToHouse = scene.createEntity("goToHouse");
		goToHouse.add(Creator.text().setText("> enter house").setFont(new Font("Consolas", Font.PLAIN, 16)).setColour(Color.yellow));
		goToHouse.add(Creator.uiElement().setAnchorX(0.0).setScreenSpace(true).setAnchorY(0.9));
		goToHouse.add(Creator.clickable().setEnabled(true).setBounds(140, 20));
		goToHouse.add(new ScriptComponent((self, em, dt) -> {
			Clickable c = self.get(Clickable.class);
			if (c.clicked) {
				e.setScene(houseScene(im, e, data));
			}
		}));
		
		scene.getLightingSystem().setEnabled(false);
		scene.getLightingSystem().setAmbientDarkness(255);

		return scene;
	}

	/** A second scene demonstrating runtime scene transitions. Reuses the player from Data. */
	public static Scene houseScene(InputManager im, Engine e, Data data) {
		Scene scene = new Scene(im, e);

		Entity fpsCounter = scene.createEntity("fps");
		fpsCounter.add(Creator.text()
		    .setText("fps: --")
		    .setFont(new Font("Consolas", Font.PLAIN, 16))
		    .setColour(Color.yellow));
		fpsCounter.add(Creator.uiElement()
		    .setScreenSpace(true)
		    .setAnchorX(0.0)
		    .setAnchorY(0.05));
		fpsCounter.add(new ScriptComponent(new Script() {
		    long lastTime = System.nanoTime();
		    int frames = 0;
		    int fps = 0;

		    @Override
		    public void update(Entity self, EntityManager em, double dt) {
		        frames++;
		        long now = System.nanoTime();
		        if (now - lastTime >= 1_000_000_000L) {
		            fps = frames;
		            frames = 0;
		            lastTime = now;
		        }
		        self.get(Text.class).setText("fps: " + fps);
		    }
		}));
		
		// player is loaded from data — must exist by now
		scene.addEntity(data.player);
		Entity player = data.player;
		// reposition for the house spawn and cancel any in-flight click-to-move
		player.get(Position.class).x = 20;
		player.get(Position.class).y = 20;
		player.get(InputState.class).isMovingToTarget = false;

		Entity camera = scene.createEntity("camera");
		Camera cam = new Camera().setTarget(player);
		camera.add(cam);
		cam.userOffsetY = -20;

		// house tilemap
		Entity mapEntity = scene.createEntity("tilemap");
		mapEntity.add(new Position());
		mapEntity.get(Position.class).x -= 32;
		mapEntity.add(new TileMap(8).setTileset("MYtileset.png").setMap("test.map"));

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

		// mom — scene-local, fresh each visit, never touches GameData
		Entity mom = scene.createEntity("mom");
		mom.add(new Position());
		mom.get(Position.class).x = 40;
		mom.get(Position.class).y = 40;
		mom.add(new Sprite().setImageLink("blue8bitsqr.png"));
		mom.add(new Layer().setLayerLevel(0));

		// back-to-world button
		Entity goBack = scene.createEntity("goBack");
		goBack.add(Creator.text().setText("< leave house").setFont(new Font("Consolas", Font.PLAIN, 16)).setColour(Color.yellow));
		goBack.add(Creator.uiElement().setAnchorX(0.0).setScreenSpace(true).setAnchorY(0.9));
		goBack.add(Creator.clickable().setEnabled(true).setBounds(140, 20));
		goBack.add(new ScriptComponent((self, em, dt) -> {
			Clickable c = self.get(Clickable.class);
			if (c.clicked) {
				e.setScene(myScene(im, e, data));
			}
		}));

		scene.getLightingSystem().setEnabled(false);
		scene.getLightingSystem().setAmbientDarkness(255);

		return scene;
	}

	/** Creates the player entity with movement, animation, collision, and lighting. Built once, reused across scenes. */
	private static Entity buildPlayer(Scene scene) {
		Entity player = scene.createEntity("player");
		player.add(Creator.position());
		player.add(Creator.movementValues());
		player.add(Creator.inputState().setKeyboardToMove(true));
		player.add(Creator.playerControlled());
		player.add(Creator.collision());
		player.add(Creator.rigidBody());
		player.add(Creator.faceMouse());
		player.add(Creator.layer().setLayerLevel(1));
		player.add(Creator.sprite());
		player.add(Creator.animation()
				.addAnimation("idle", "idle-animation-test.png", 3, 8, 8, 0.25, true)
				.setCurrentAnimation("idle"));

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
		return player;
	}
}