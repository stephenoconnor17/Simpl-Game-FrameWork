package entities.components;

import entities.Entity;
import entities.components.audio.AudioSource;
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
import entities.components.rendering.UIElement;
import entities.components.transform.ChildEntity;
import entities.components.transform.ParentEntity;
import entities.components.transform.Position;
import entities.components.rpgsystem.Stats;
import entities.components.util.TimeToLive;
import entities.components.world.TileEntitySpawner;
import entities.components.world.TileMap;

/**
 * Quick-access factory for all engine components.
 * Usage: entity.addComponent(Creator.position());
 */
public final class Creator {

	private Creator() {}

	// --- Transform ---
	public static Position position()             { return new Position(); }
	public static ChildEntity childEntity()       { return new ChildEntity(); }
	public static ParentEntity parentEntity()     { return new ParentEntity(); }

	// --- Movement ---
	public static MovementValues movementValues() { return new MovementValues(); }

	// --- Physics ---
	public static RigidBody rigidBody()           { return new RigidBody(); }
	public static Collision collision()            { return new Collision(); }
	public static Pickup pickup()                 { return new Pickup(); }

	// --- Rendering ---
	public static Sprite sprite()                 { return new Sprite(); }
	public static Layer layer()                   { return new Layer(); }
	public static Animation animation()           { return new Animation(); }
	public static Camera camera()                 { return new Camera(); }
	public static Light light()                   { return new Light(); }
	public static FaceMouse faceMouse()           { return new FaceMouse(); }
	public static FaceEntity faceEntity(Entity e)         { return new FaceEntity(e); }
	public static RotateViewToMouse rotateViewToMouse() { return new RotateViewToMouse(); }
	public static UIElement uiElement()               { return new UIElement(); }

	// --- Input ---
	public static Clickable clickable()               { return new Clickable(); }
	public static PlayerControlled playerControlled() { return new PlayerControlled(); }
	public static InputState inputState()         { return new InputState(); }

	// --- Audio ---
	public static AudioSource audioSource()       { return new AudioSource(); }

	// --- World ---
	public static TileMap tileMap(int tileSize)               { return new TileMap(tileSize); }
	
	// --- RPG ---
	public static Stats stats()                   { return new Stats(); }

	// --- Script ---
	public static ScriptComponent script(Script s) { return new ScriptComponent(s); }

	// --- Util ---
	public static TimeToLive timeToLive()         { return new TimeToLive(); }
}
