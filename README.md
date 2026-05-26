# Simpl Engine

A 2D game engine in pure Java (AWT/Swing). No frameworks, no dependencies. Entity-Component-System architecture built from scratch with pixel-art rendering, physics, tilemaps, audio, and scripting.

Simpl gives you the primitives to build 2D games. It doesn't try to be an RPG engine or a platformer engine. You compose entities from components, write systems that operate on them, and the engine handles the loop, the rendering pipeline, and the physics. Genre-specific features (inventory, dialogue, combat) are things you build *with* Simpl, not things baked into it. Early plans for built-in RPG and genre systems were deliberately dissolved because they'd constrain the engine to a subset of what it could be. The `Stats` component exists as a minimal example, not a framework.

## Design Decisions

### Why ECS

Traditional game engines encourage deep inheritance hierarchies: `GameObject -> Character -> Player -> MagePlayer`. Every new entity type requires a new class, and cross-cutting concerns (an NPC that's also collidable, also lit, also animated) turn into multiple-inheritance nightmares or decorator soup.

Simpl sidesteps this entirely. `Entity` is `final`. You cannot subclass it. Every entity is the same class with a different bag of components attached. A player is an entity with `Position`, `MovementValues`, `InputState`, `PlayerControlled`, `Collision`, `RigidBody`, and `Sprite`. A wall is an entity with `Position`, `Collision`, and `RigidBody` set to immovable. The difference is data, not type. Systems iterate over entities that have the components they care about and ignore everything else.

This means you never paint yourself into an inheritance corner. Adding a new behaviour to an existing entity is `entity.add(new Light())`, not a refactor.

### Why Position Is Always World-Space

`Position.x` and `Position.y` are always world coordinates. There is no local-space position. When an entity is a child of another entity, the local offset lives on `ChildOf.offsetX` / `ChildOf.offsetY`, and `MovementSystem` writes the resolved world position back to `Position` every frame:

```
pos.x = parentPos.x + childLink.offsetX;
pos.y = parentPos.y + childLink.offsetY;
```

The parenting model is single-level: a child has one parent and a flat offset. There is no recursive transform chain. `MovementSystem` resolves `parent + offset` in one step and writes the result to `Position`. Every other system — physics, rendering, audio, click detection, lighting — just reads `pos.x` and `pos.y` directly without knowing whether the entity has a parent. The hierarchy resolution happens once, in `MovementSystem`, before anything else needs the result.

### Why a Fixed-Timestep Accumulator

The game loop uses a fixed-timestep accumulator rather than passing raw frame delta to systems:

```java
accumulator += frameTime;
while (accumulator >= FIXED_STEP) {
    update(FIXED_STEP);
    accumulator -= FIXED_STEP;
}
render();
```

With a variable timestep, physics behaves differently at different frame rates. An entity moving at `speed * dt` will take slightly different paths depending on when collision detection runs relative to the frame boundary, because penetration depth and resolution forces scale with `dt`. At 30 FPS an entity might tunnel through a thin wall that it bounces off cleanly at 60 FPS.

The accumulator guarantees every `update()` call receives exactly `1/60` of a second. Physics, movement, and collision resolution are deterministic regardless of rendering speed. Rendering is decoupled: it runs once per frame at whatever rate the hardware allows, but the simulation ticks at a constant rate. The 0.25-second clamp on `frameTime` prevents a spiral-of-death when the game hitches (e.g., a debugger pause) from causing dozens of catch-up ticks.

### Why No Genre Primitives

Early roadmap phases included built-in RPG systems (combat, inventory, dialogue trees) and platformer primitives (gravity controllers, jump arcs). These were scrapped. A 2D engine that bundles genre-specific systems either becomes opinionated about game design or accumulates dead code for every genre it doesn't serve.

Simpl provides the building blocks: entities, physics, rendering, input, audio, scripting. An RPG combat system is a `ScriptComponent` that reads `Stats` and `Collision`. A platformer jump is a `ScriptComponent` that modifies `MovementValues.velocityY`. These belong in your game, not in the engine, because the engine can't know what tradeoffs your game needs.

## Architecture

```
src/
+-- core/             Engine loop, Window, GamePanel, Scene, PixelStyle
+-- entities/
|   +-- Entity, EntityManager
|   +-- components/
|   |   +-- Creator.java           Static factory for all components
|   |   +-- Script.java            @FunctionalInterface for lambda scripts
|   |   +-- ScriptComponent.java   Script wrapper component
|   |   +-- Component.java         Base class with dirty-flag support
|   |   +-- transform/      Position, ChildOf, ParentOf
|   |   +-- movement/       MovementValues
|   |   +-- physics/         Collision, RigidBody, Pickup
|   |   +-- rendering/       Sprite, Animation, Camera, Layer, Light,
|   |   |                    UIElement, Text, FaceMouse, FaceEntity,
|   |   |                    RotateViewToMouse
|   |   +-- input/           PlayerControlled, InputState, Clickable
|   |   +-- audio/           AudioSource
|   |   +-- world/           TileMap, TileEntitySpawner
|   |   +-- rpgsystem/       Stats
|   |   +-- util/            TimeToLive
|   +-- systems/
|       ClickSystem, PlayerControlSystem, MovementSystem,
|       ScriptSystem, PhysicsSystem, PickupSystem,
|       AnimationSystem, AudioSystem, TimeToLiveSystem,
|       TileMapSystem, RenderingSystem, LightingSystem
+-- input/             InputManager, Keyboard, Mouse
+-- utils/             TileMapUtils
+-- exceptions/        DuplicateComponentException
```

### System Execution Order

Update systems run in this order every tick:

1. **ClickSystem** — detect hover/press/click before any game logic reads them
2. **PlayerControlSystem** — map raw input to movement intent
3. **MovementSystem** — apply velocity, resolve parent-child positions
4. **ScriptSystem** — run user scripts with up-to-date positions
5. **PhysicsSystem** — detect and resolve collisions
6. **PickupSystem** — handle item collection from collision results
7. **AnimationSystem** — advance frames, swap sprite images
8. **AudioSystem** — start/stop clips, update spatial audio
9. **TimeToLiveSystem** — decrement timers, destroy expired entities

Render systems run after all updates:

1. **TileMapSystem** — draw tile layers with camera transform
2. **RenderingSystem.renderWorld** — draw world entities with camera transform
3. **LightingSystem** — composite darkness overlay with light subtraction
4. **RenderingSystem.renderUI** — draw screen-space UI without camera transform

This ordering is load-bearing. Player input feeds movement, movement resolves positions before scripts read them, scripts run before physics so they can set up velocities, physics runs before pickup so collision lists are populated, and animation swaps sprites before rendering draws them. UI renders last so it always draws on top of the lighting overlay.

### Rendering Pipeline

The engine renders to a virtual canvas at the resolution defined by `PixelStyle`, then scales up to the physical window with nearest-neighbour interpolation. This gives pixel-art games a consistent look regardless of monitor resolution: a sprite drawn at 8x8 pixels stays sharp at any window size.

The camera is its own entity with a `Camera` component. It's decoupled from the player and from the rendering system's internals. Any entity can be the camera target, or the camera can be free-floating. Zoom, rotation, and manual offsets are all fields on the `Camera` component.

RenderingSystem is split into two passes. `renderWorld()` applies the full camera transform (zoom, rotation, offset) and draws all world-space entities sorted by layer. `renderUI()` skips the camera transform entirely and positions screen-space entities by anchor percentage. The split means lighting composites between the two passes, so UI always draws on top of darkness.

## Quick Start

```java
public static void main(String[] args) {
    Window w = new Window("My Game");
    Engine e = new Engine(w.getRenderSurface(), PixelStyle.BIT_8);
    e.setScene(buildScene(e.getInputManager(), e));
    w.setEngine(e);
    e.start();
}
```

Build a scene by creating entities and attaching components:

```java
Scene scene = new Scene(inputManager, engine);

Entity player = scene.createEntity("player");
player.add(new Position());
player.add(new MovementValues());
player.add(new InputState().setKeyboardToMove(true));
player.add(new PlayerControlled());
player.add(new Collision());
player.add(new RigidBody());
player.add(new Sprite().setImageLink("player.png"));
player.add(new Light().setRadius(18).setIntensity(0.5));

Entity camera = scene.createEntity("camera");
camera.add(new Camera().setTarget(player));
```

The `Creator` factory provides IDE autocomplete for all components:

```java
player.add(Creator.position());
player.add(Creator.movementValues());
player.add(Creator.inputState().setKeyboardToMove(true));
player.add(Creator.sprite().setImageLink("player.png"));
player.add(Creator.light().setRadius(18).setIntensity(0.5));
```

Attach behaviour with inline scripts:

```java
entity.add(new ScriptComponent((self, entityManager, dt) -> {
    Position pos = self.get(Position.class);
    pos.x += 10 * dt;
}));
```

Load a tilemap and spawn wall entities from tile data:

```java
Entity map = scene.createEntity("tilemap");
map.add(new Position());
map.add(new TileMap(8).setTileset("tiles.png").setMap("level.map"));

TileMapUtils.spawnEntities(map.get(TileMap.class), scene, 0, 0, (sc, tileIndex, wx, wy) -> {
    if (tileIndex == 1) {
        Entity wall = sc.createEntity("wall");
        wall.add(new Position());
        wall.get(Position.class).x = wx;
        wall.get(Position.class).y = wy;
        wall.add(new Collision());
        wall.add(new RigidBody().setMovable(false));
    }
});
```

Enable lighting:

```java
scene.getLightingSystem().setEnabled(true);
scene.getLightingSystem().setAmbientDarkness(200);
```

Screen-space UI with anchor positioning:

```java
Entity hud = scene.createEntity("hud");
hud.add(Creator.text().setText("Score: 0")
    .setFont(new Font("Consolas", Font.PLAIN, 16))
    .setColour(Color.WHITE));
hud.add(Creator.uiElement()
    .setScreenSpace(true)
    .setAnchorX(0.0)    // 0.0 = left, 0.5 = center, 1.0 = right
    .setAnchorY(0.0));  // 0.0 = top,  0.5 = center, 1.0 = bottom
```

Scene transitions via `engine.setScene()`:

```java
Entity door = scene.createEntity("door");
door.add(Creator.clickable().setEnabled(true).setBounds(16, 16));
door.add(new ScriptComponent((self, em, dt) -> {
    if (self.get(Clickable.class).clicked) {
        engine.setScene(buildNextScene(inputManager, engine, data));
    }
}));
```

Persistent entities survive scene transitions via a `Data` object:

```java
Data data = new Data();
data.player = buildPlayer(scene);  // built once
// In the next scene:
scene.addEntity(data.player);      // reused, not rebuilt
```

## Component Reference

### Transform
| Component | Purpose |
|-----------|---------|
| `Position` | World-space x, y, rotation. Every positioned entity needs this. |
| `ChildOf` | Links to a parent entity with offsetX/offsetY. Position is resolved to world-space by MovementSystem. Single-level only. `inheritRotation` field exists but is not yet wired into any system. |
| `ParentOf` | Inverse marker on the parent side (currently informational). |

### Movement
| Component | Purpose |
|-----------|---------|
| `MovementValues` | Speed (units/sec) and current velocityX/velocityY. MovementSystem applies velocity * dt to Position each tick. |

### Physics
| Component | Purpose |
|-----------|---------|
| `Collision` | Shape (BOX or CIRCLE), dimensions, layer/mask bit flags for filtering, solid flag. `collidedWith` list populated by PhysicsSystem each frame. |
| `RigidBody` | Marks entity for collision resolution. `movable` controls whether it's pushed or acts as static geometry. |
| `Pickup` | Tags an entity as collectible. PickupSystem removes it on player contact. |

### Rendering
| Component | Purpose |
|-----------|---------|
| `Sprite` | Image to draw. `setImageLink("file.png")` loads from `res/sprites/`. |
| `Animation` | Named animation sequences from horizontal sprite sheets. AnimationSystem swaps Sprite.image per frame. |
| `Camera` | Marks entity as the active camera. Target entity, zoom, rotation, manual offset. |
| `Layer` | Integer draw order. Higher values render on top. |
| `Light` | Point light with radius, intensity, and colour. LightingSystem subtracts from ambient darkness. |
| `UIElement` | Marks entity as UI. `screenSpace=true` renders without camera transform using anchorX/anchorY (0.0-1.0). Children inherit screenSpace from root parent. |
| `Text` | Renders a string with configurable font, size, and colour. Works in world-space and screen-space. |
| `FaceMouse` | Rotates entity to face the mouse cursor. |
| `FaceEntity` | Rotates entity to face another entity. |
| `RotateViewToMouse` | FPS-style camera rotation from mouse delta. Locks the cursor. |

### Input
| Component | Purpose |
|-----------|---------|
| `PlayerControlled` | Tags entity for PlayerControlSystem processing. |
| `InputState` | Current movement flags, mouse position, click-to-move target. `keyboardToMove` / `clickToMove` toggle input modes. |
| `Clickable` | Mouse interaction. `clicked` (one-frame), `hovered`, `pressed` (continuous). Explicit bounds or falls back to Sprite dimensions. Screen-space clickables consume input before world clickables. |

### Audio
| Component | Purpose |
|-----------|---------|
| `AudioSource` | Audio playback with flag protocol (set `play=true` to start). Spatial mode attenuates volume by distance and pans stereo relative to a listener entity, accounting for camera rotation. |

### World
| Component | Purpose |
|-----------|---------|
| `TileMap` | Tile grid data and tileset reference. Lazy-loaded by TileMapSystem. |
| `TileEntitySpawner` | Functional interface for spawning entities from tile indices. |

### Utility
| Component | Purpose |
|-----------|---------|
| `TimeToLive` | Auto-destroys the entity after a duration in seconds. |
| `Stats` | Minimal RPG stats (health, attack, defense, level, xp). An example, not a framework. |
| `ScriptComponent` | Wraps a `Script` lambda. Receives self, entity manager, and dt each frame. |

## Pixel Styles

| Style | Resolution |
|-------|-----------|
| `BIT_8` | 256 x 144 |
| `BIT_16` | 320 x 180 |
| `BIT_32` | 480 x 270 |
| `BIT_64` | 640 x 360 |
| `BIT_128` | 960 x 540 |
| `BIT_256` | 1920 x 1080 |

## Build & Run

Plain Java project. No Maven, no Gradle. Open in Eclipse (or any IDE), compile, and run `core.Main`.

```
javac -d out $(find src -name "*.java")
java -cp out core.Main
```

Sprites go in `res/sprites/`, maps in `res/maps/`, audio in `res/audio/`.

## Network-Ready Dirty Tracking

Components extend a base `Component` class with a `dirty` flag. Systems call `markDirty()` after modifying component state. The flag is gated behind `Engine.isOnline()`, so single-player games pay zero overhead. Entities are created through `scene.createEntity()` to guarantee monotonically increasing IDs suitable for network sync. This is infrastructure, not a networking implementation. It means when you add multiplayer, the data layer is already instrumented.

## Limitations

- **Single-level parenting only.** `ChildOf` links one child to one parent with a flat offset. There are no nested transform chains or recursive hierarchy resolution.
- **`inheritRotation` is reserved but not implemented.** The field exists on `ChildOf` but no system reads it yet.
- **No automated tests.** The engine is verified manually via `core.Main`. There is no test suite.
