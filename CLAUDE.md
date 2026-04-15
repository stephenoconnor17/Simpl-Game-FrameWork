# Simpl Engine — Project Directive

## What This Is
Simpl is a 2D game engine written in Java using AWT/Swing. It follows an Entity-Component-System (ECS) architecture where entities are data bags of components and systems contain all the logic.

## Architecture
- **Core**: Engine (game loop, virtual canvas rendering), Window, GamePanel, Scene, PixelStyle
- **ECS**: Entity (final class, not abstract — this is ECS, not OOP), EntityManager, Component base class
- **Components**: Organized by domain — `transform/`, `movement/`, `physics/`, `rendering/`, `input/`, `world/`, `audio/`, `rpgsystem/`, `util/`
- **Creator**: Static factory class (`Creator.java`) for all components — enables IDE autocomplete discovery
- **Systems**: PlayerControlSystem, MovementSystem, PhysicsSystem, PickupSystem, ScriptSystem, AnimationSystem, AudioSystem, RenderingSystem, TileMapSystem, LightingSystem, TimeToLiveSystem
- **Input**: InputManager coordinates Keyboard and Mouse handlers

## Key Design Decisions
- Entities are final — no subclassing. All behavior comes from components + systems.
- PixelStyle defines a virtual canvas resolution; the engine renders at virtual res and scales up with nearest-neighbor for a pixel-art look.
- Camera is its own entity, decoupled from rendered entities.
- Script is a @FunctionalInterface so scripts can be lambdas.
- Physics supports movable/immovable rigid bodies, layer filtering, and circle/box/OBB collision.
- UI elements are entities with a `UIElement` component. `screenSpace` flag controls screen-pinned vs world-space rendering. Children inherit `screenSpace` from their root parent.
- Animation supports multiple named animations per entity, loaded from horizontal sprite sheets. AnimationSystem swaps sprite images per frame.
- AudioSource supports spatial audio — volume attenuates by distance and stereo pans relative to a listener entity, accounting for camera rotation.
- System execution order matters: PlayerControl -> Movement -> Script -> Physics -> Pickup -> Animation -> Audio -> TimeToLive (update), TileMap -> Rendering -> Lighting (render).

## Conventions
- Components are plain data classes with public fields and fluent setters (e.g. `setMovable(true)`).
- Systems implement `GameSystem` interface.
- Scene wires up all systems and holds the EntityManager.
- Main.java is the test/demo entry point — it sets up a sample scene.
- Sprites and maps live in `res/sprites/` and `res/maps/`.

## Build
- This is an Eclipse workspace Java project (no Maven/Gradle). Just compile and run `core.Main`.
- The `node_modules/`, `package.json`, `package-lock.json`, `generate_doc.mjs`, and `.docx` files are for documentation generation — not part of the engine.
