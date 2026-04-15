# Simpl Engine

A lightweight 2D game engine in pure Java (AWT/Swing). No frameworks, no dependencies — just an ECS architecture built from scratch with pixel-art rendering, physics, tilemaps, and scripting.

## Features

- **Entity-Component-System** — Entities are data bags. Components hold state. Systems run the logic. No inheritance hierarchies, no god objects.
- **Pixel-art rendering** — Virtual canvas at configurable resolutions (256x144 up to 1920x1080) scaled with nearest-neighbor interpolation. Pick a `PixelStyle` and the engine handles the rest.
- **Camera system** — Independent camera entity with zoom, rotation, and entity tracking. Decoupled from player logic.
- **Physics** — Circle, box, and OBB collision detection. Movable/immovable rigid bodies. Layer-based collision filtering. Solid vs. trigger colliders.
- **Tilemap system** — Load `.map` files, render with tilesets, spawn entities from tile data (walls, triggers, etc.).
- **Scripting** — `@FunctionalInterface` scripts attach to entities as lambdas. Full access to the entity and the entity manager each frame.
- **Input** — Keyboard and mouse handling with screen-to-world coordinate transformation that respects camera zoom/rotation.
- **Lighting** — Dynamic point lights with configurable radius, intensity, and color. Ambient darkness overlay with per-light radial gradient subtraction.
- **Audio** — AudioSource component with looping, volume control, and spatial audio. Spatial mode attenuates volume by distance and pans stereo left/right relative to a listener entity, accounting for camera rotation.
- **Creator factory** — Static factory class (`Creator`) for all components. Enables fast iteration via IDE autocomplete — type `Creator.` to see every available component.
- **Animation** — Multiple named animations per entity, loaded from horizontal sprite sheets. Frame duration, looping, and runtime switching via `setCurrentAnimation()`. The system swaps the entity's sprite image each frame automatically.
- **UI elements** — Entities can be marked as UI via the `UIElement` component. A `screenSpace` flag controls whether they render in screen coordinates (HUD, menus) or world coordinates (health bars above enemies). Children inherit `screenSpace` from their parent.
- **Parent-child entities** — Transform hierarchy via `ParentEntity`/`ChildEntity` components.

## Quick Start

```java
public static void main(String[] args) {
    Window w = new Window("My Game");
    Engine e = new Engine(w.getRenderSurface(), PixelStyle.BIT_8);
    e.setScene(buildScene(e.getInputManager()));
    w.setEngine(e);
    e.start();
}
```

Build a scene by creating entities and attaching components:

```java
Scene scene = new Scene(inputManager);

Entity player = new Entity(0, "player");
player.add(new Position());
player.add(new MovementValues());
player.add(new InputState().setKeyboardToMove(true));
player.add(new PlayerControlled());
player.add(new Collision());
player.add(new RigidBody());
player.add(new Sprite().setImageLink("player.png"));
player.add(new Light().setRadius(18).setIntensity(0.5));

Entity camera = new Entity(0, "camera");
camera.add(new Camera().setTarget(player));

scene.addEntity(player);
scene.addEntity(camera);
```

Alternatively, use the `Creator` factory for IDE autocomplete — type `Creator.` to see every available component:

```java
player.add(Creator.position());
player.add(Creator.movementValues());
player.add(Creator.inputState().setKeyboardToMove(true));
player.add(Creator.sprite().setImageLink("player.png"));
player.add(Creator.light().setRadius(18).setIntensity(0.5));
```

Attach behavior with inline scripts:

```java
entity.add(new ScriptComponent((self, entityManager, dt) -> {
    Position pos = self.get(Position.class);
    pos.x += 10 * dt;
}));
```

Load a tilemap and spawn wall entities from tile data:

```java
Entity map = new Entity(0, "tilemap");
map.add(new Position());
map.add(new TileMap(8).setTileset("tiles.png").setMap("level.map"));
scene.addEntity(map);

TileMapUtils.spawnEntities(map.get(TileMap.class), entityManager, 0, 0, (tileIndex, wx, wy) -> {
    if (tileIndex == 1) {
        Entity wall = new Entity(0, "wall");
        wall.add(new Position());
        wall.get(Position.class).x = wx;
        wall.get(Position.class).y = wy;
        wall.add(new Collision());
        wall.add(new RigidBody().setMovable(false));
        return wall;
    }
    return null;
});
```

Enable lighting with ambient darkness:

```java
scene.getLightingSystem().setEnabled(true);
scene.getLightingSystem().setAmbientDarkness(200);
```

Animate an entity with multiple directional animations:

```java
player.add(Creator.animation()
    .addAnimation("walk_down",  "player_walk_down.png",  4, 32, 32, 0.15, true)
    .addAnimation("walk_up",    "player_walk_up.png",    4, 32, 32, 0.15, true)
    .addAnimation("idle",       "player_idle.png",       2, 32, 32, 0.5,  true)
    .setCurrentAnimation("idle"));

// Switch animation based on input (via script)
player.add(Creator.script((self, em, dt) -> {
    InputState input = self.get(InputState.class);
    Animation anim = self.get(Animation.class);
    if (input.up)        anim.setCurrentAnimation("walk_up");
    else if (input.down) anim.setCurrentAnimation("walk_down");
    else                 anim.setCurrentAnimation("idle");
}));
```

Add a screen-space HUD element:

```java
Entity hud = new Entity(0, "hud");
hud.add(Creator.position().setXY(10, 10));
hud.add(Creator.sprite().setImageLink("ui/healthbar.png"));
hud.add(Creator.uiElement()); // screenSpace=true by default
scene.addEntity(hud);
```

Or a world-space UI element (health bar above an enemy) — children inherit `screenSpace` from their parent:

```java
Entity nameplate = new Entity(0, "nameplate");
nameplate.add(Creator.position().setXY(0, -20));
nameplate.add(Creator.sprite().setImageLink("ui/nameplate.png"));
nameplate.add(Creator.uiElement().setScreenSpace(false));
nameplate.add(Creator.parentEntity().setParentEntity(enemy));
scene.addEntity(nameplate);
```

Play a spatial sound that pans and fades with distance:

```java
Entity torch = new Entity(0, "torch");
torch.add(Creator.position().setXY(200, 300));
torch.add(Creator.audioSource()
    .setFilePath("torch_crackle.wav")
    .setSpatial(true)
    .setListener(player)
    .setMaxDistance(200)
    .setLoop(true)
    .setPlay(true));
scene.addEntity(torch);
```

## Architecture

```
src/
├── core/           Engine, Window, GamePanel, Scene, PixelStyle
├── entities/
│   ├── Entity, EntityManager
│   ├── components/
│   │   ├── Creator.java         Static factory for all components
│   │   ├── Script.java          Functional interface for lambdas
│   │   ├── ScriptComponent.java Script wrapper component
│   │   ├── transform/    Position, ParentEntity, ChildEntity
│   │   ├── movement/     MovementValues
│   │   ├── physics/      Collision, RigidBody, Pickup
│   │   ├── rendering/    Sprite, Animation, Camera, Layer, Light, UIElement, FaceMouse, FaceEntity, RotateViewToMouse
│   │   ├── input/        PlayerControlled, InputState
│   │   ├── world/        TileMap, TileEntitySpawner
│   │   ├── audio/        AudioSource
│   │   ├── rpgsystem/    Stats
│   │   └── util/         TimeToLive
│   └── systems/
│       PlayerControlSystem → MovementSystem → ScriptSystem →
│       PhysicsSystem → PickupSystem → AnimationSystem →
│       AudioSystem → TimeToLiveSystem (update)
│       TileMapSystem → RenderingSystem → LightingSystem (render)
├── input/          InputManager, Keyboard, Mouse
├── utils/          TileMapUtils
└── exceptions/     DuplicateComponentException
```

System execution order is intentional. Player input feeds movement, scripts run before physics resolves, animation updates sprites before rendering, and rendering happens last. Screen-space UI entities render after world entities (no camera transform applied).

## Pixel Styles

| Style      | Resolution  |
|------------|-------------|
| `BIT_8`    | 256 x 144   |
| `BIT_16`   | 320 x 180   |
| `BIT_32`   | 480 x 270   |
| `BIT_64`   | 640 x 360   |
| `BIT_128`  | 960 x 540   |
| `BIT_256`  | 1920 x 1080 |

## Build & Run

This is a plain Java project (no Maven/Gradle). Open in Eclipse (or any IDE), compile, and run `core.Main`.

```
javac -d out $(find src -name "*.java")
java -cp out core.Main
```

Sprites go in `res/sprites/`, maps in `res/maps/`.

## Design Principles

- **Composition over inheritance** — `Entity` is `final`. All behavior comes from combining components and systems.
- **Systems own the logic** — Components are plain data with public fields and fluent setters. Zero logic in components.
- **Scenes are self-contained** — A `Scene` wires up all systems and holds the `EntityManager`. Swap scenes to change game states.
- **No external dependencies** — Pure Java standard library. The engine runs anywhere Java runs.
