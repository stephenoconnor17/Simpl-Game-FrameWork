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
- **Audio** — AudioSource and MusicSource components with play-on-spawn and event-driven playback.
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

Entity camera = new Entity(0, "camera");
camera.add(new Camera().setTarget(player));

scene.addEntity(player);
scene.addEntity(camera);
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
map.add(new TileMap(8).setTileset("tiles.png").loadMap("level.map"));
scene.addEntity(map);

map.get(TileMap.class).spawnEntities(entityManager, 0, 0, (tileIndex, wx, wy) -> {
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

## Architecture

```
src/
├── core/           Engine, Window, GamePanel, Scene, PixelStyle
├── entities/
│   ├── Entity, EntityManager
│   ├── components/
│   │   ├── transform/    Position, ParentEntity, ChildEntity
│   │   ├── movement/     MovementValues
│   │   ├── physics/      Collision, RigidBody, Pickup
│   │   ├── rendering/    Sprite, Animation, Camera, Layer, FaceMouse, FaceEntity
│   │   ├── input/        PlayerControlled, InputState
│   │   ├── world/        TileMap, TileEntitySpawner
│   │   ├── audio/        AudioSource, MusicSource, PlayOnSpawn, PlayOnEvent
│   │   ├── rpgsystem/    Stats
│   │   └── util/         TimeToLive
│   └── systems/
│       PlayerControlSystem → MovementSystem → ScriptSystem →
│       PhysicsSystem → PickupSystem → TimeToLiveSystem (update)
│       TileMapSystem → RenderingSystem (render)
├── input/          InputManager, Keyboard, Mouse
├── tiles/          Tile, TileMap, TileLayer, TileRenderer
├── ui/             UIElement, UIManager
└── exceptions/     DuplicateComponentException
```

System execution order is intentional. Player input feeds movement, scripts run before physics resolves, and rendering happens last.

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
