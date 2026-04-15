import {
  Document, Packer, Paragraph, TextRun, HeadingLevel, AlignmentType,
  TableOfContents, BorderStyle, Table, TableRow, TableCell, WidthType,
  ShadingType, convertInchesToTwip, Tab, TabStopType, TabStopPosition,
  LevelFormat, PageBreak
} from "docx";
import fs from "fs";

// ── helpers ──────────────────────────────────────────────────────────
function heading(text, level = HeadingLevel.HEADING_1) {
  return new Paragraph({ text, heading: level, spacing: { before: 300, after: 120 } });
}
function h1(t) { return heading(t, HeadingLevel.HEADING_1); }
function h2(t) { return heading(t, HeadingLevel.HEADING_2); }
function h3(t) { return heading(t, HeadingLevel.HEADING_3); }

function para(text) {
  return new Paragraph({
    children: [new TextRun({ text, size: 22 })],
    spacing: { after: 120 },
  });
}

function bold(text) {
  return new Paragraph({
    children: [new TextRun({ text, bold: true, size: 22 })],
    spacing: { after: 80 },
  });
}

function bullet(text, level = 0) {
  return new Paragraph({
    children: [new TextRun({ text, size: 22 })],
    bullet: { level },
    spacing: { after: 60 },
  });
}

function code(lines) {
  return lines.map(line =>
    new Paragraph({
      children: [new TextRun({ text: line, font: "Consolas", size: 18 })],
      spacing: { after: 0 },
      shading: { type: ShadingType.SOLID, color: "F2F2F2" },
      indent: { left: convertInchesToTwip(0.3) },
    })
  );
}

function codeBlock(str) {
  return code(str.split("\n"));
}

function pageBreak() {
  return new Paragraph({ children: [new PageBreak()] });
}

// ── document ─────────────────────────────────────────────────────────
const doc = new Document({
  styles: {
    default: {
      document: {
        run: { font: "Calibri", size: 22 },
      },
    },
  },
  sections: [{
    properties: {},
    children: [

      // ═══════════════════════════════════════════════════════════════
      // TITLE PAGE
      // ═══════════════════════════════════════════════════════════════
      new Paragraph({ spacing: { before: 4000 } }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "Simpl Engine", bold: true, size: 56 })],
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "Technical Architecture Document", size: 32, italics: true })],
        spacing: { after: 200 },
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "A 2D Game Engine Built on the Entity-Component-System Pattern", size: 24 })],
        spacing: { after: 600 },
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "March 2026", size: 22, color: "666666" })],
      }),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // TABLE OF CONTENTS (manual)
      // ═══════════════════════════════════════════════════════════════
      h1("Table of Contents"),
      para("1.  Overview & Philosophy"),
      para("2.  Project Structure"),
      para("3.  Core Architecture"),
      para("    3.1  Engine & Game Loop"),
      para("    3.2  Window & GamePanel"),
      para("    3.3  Scene"),
      para("4.  Entity-Component-System (ECS)"),
      para("    4.1  Entity"),
      para("    4.2  Component (Base)"),
      para("    4.3  EntityManager"),
      para("    4.4  GameSystem Interface"),
      para("5.  Components — In Depth"),
      para("    5.1  Transform: Position"),
      para("    5.2  Transform: ParentEntity & ChildEntity"),
      para("    5.3  Movement: MovementValues"),
      para("    5.4  Input: InputState & PlayerControlled"),
      para("    5.5  Physics: Collision"),
      para("    5.6  Physics: RigidBody"),
      para("    5.7  Physics: Pickup"),
      para("    5.8  Rendering: Sprite"),
      para("    5.9  Rendering: Layer"),
      para("    5.10 Rendering: Camera"),
      para("    5.11 Rendering: FaceMouse & FaceEntity"),
      para("    5.12 Rendering: RotateViewToMouse"),
      para("    5.13 Rendering: Light"),
      para("    5.14 World: TileMap"),
      para("    5.15 Scripting: Script & ScriptComponent"),
      para("    5.16 Audio: AudioSource"),
      para("    5.17 RPG: Stats"),
      para("    5.18 Util: TimeToLive"),
      para("6.  Systems — In Depth"),
      para("    6.1  PlayerControlSystem"),
      para("    6.2  MovementSystem"),
      para("    6.3  ScriptSystem"),
      para("    6.4  PhysicsSystem"),
      para("    6.5  PickupSystem"),
      para("    6.6  TimeToLiveSystem"),
      para("    6.7  TileMapSystem"),
      para("    6.8  RenderingSystem"),
      para("    6.9  LightingSystem"),
      para("7.  Input Management"),
      para("8.  System Execution Order & Data Flow"),
      para("9.  Creator — Component Factory"),
      para("10. Example: Building a Scene"),
      para("11. Utilities"),
      para("    11.1 TileMapUtils"),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 1. OVERVIEW
      // ═══════════════════════════════════════════════════════════════
      h1("1. Overview & Philosophy"),
      para("Simpl is a lightweight 2D game engine written in pure Java using AWT/Swing for rendering. It follows the Entity-Component-System (ECS) architectural pattern, which favours composition over inheritance. In ECS:"),
      bullet("Entities are unique identifiers (IDs with a name) that act as containers for components."),
      bullet("Components are plain data objects — they hold state but contain no behaviour logic."),
      bullet("Systems contain all the logic. Each system iterates over entities that possess the components it cares about, reads their data, and updates it."),
      para("This separation means new gameplay features can be added by creating a new Component (data) and a new System (logic), without modifying existing classes. It also makes the engine easy to reason about: the update order of systems is explicit, and data flows in one direction through the pipeline."),
      para("The engine targets a retro pixel-art aesthetic. It renders to a small virtual canvas (configurable via PixelStyle) and scales it up to the full window using nearest-neighbour interpolation, preserving crisp pixel edges."),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 2. PROJECT STRUCTURE
      // ═══════════════════════════════════════════════════════════════
      h1("2. Project Structure"),
      ...codeBlock(
`src/
├── core/
│   ├── Main.java              — Entry point & test scene setup
│   ├── Engine.java            — Game loop, rendering pipeline
│   ├── GamePanel.java         — AWT Canvas (render surface)
│   ├── Window.java            — JFrame fullscreen wrapper
│   ├── Scene.java             — Holds EntityManager + all systems
│   └── PixelStyle.java        — Virtual resolution presets
├── entities/
│   ├── Entity.java            — Component container with ID
│   ├── EntityManager.java     — Entity lifecycle & lookups
│   ├── components/
│   │   ├── Component.java         — Abstract base
│   │   ├── Creator.java           — Static factory for all components
│   │   ├── Script.java            — Functional interface
│   │   ├── ScriptComponent.java   — Script wrapper component
│   │   ├── transform/
│   │   │   ├── Position.java
│   │   │   ├── ParentEntity.java
│   │   │   └── ChildEntity.java
│   │   ├── movement/
│   │   │   └── MovementValues.java
│   │   ├── input/
│   │   │   ├── InputState.java
│   │   │   └── PlayerControlled.java
│   │   ├── physics/
│   │   │   ├── Collision.java
│   │   │   ├── RigidBody.java
│   │   │   └── Pickup.java
│   │   ├── rendering/
│   │   │   ├── Sprite.java
│   │   │   ├── Layer.java
│   │   │   ├── Camera.java
│   │   │   ├── Light.java
│   │   │   ├── FaceMouse.java
│   │   │   ├── FaceEntity.java
│   │   │   └── RotateViewToMouse.java
│   │   ├── world/
│   │   │   ├── TileMap.java
│   │   │   └── TileEntitySpawner.java (functional interface)
│   │   ├── audio/
│   │   │   └── AudioSource.java
│   │   └── rpgsystem/
│   │       └── Stats.java
│   └── systems/
│       ├── GameSystem.java        — System interface
│       ├── PlayerControlSystem.java
│       ├── MovementSystem.java
│       ├── PhysicsSystem.java
│       ├── PickupSystem.java
│       ├── ScriptSystem.java
│       ├── RenderingSystem.java
│       ├── LightingSystem.java
│       ├── TimeToLiveSystem.java
│       └── TileMapSystem.java
├── input/
│   ├── InputManager.java
│   ├── Keyboard.java
│   └── Mouse.java
├── utils/
│   └── TileMapUtils.java       — Static tile map loading & entity spawning
└── exceptions/
    └── DuplicateComponentException.java

res/
├── maps/
│   └── test.map
└── sprites/
    ├── blue8bitsqr.png
    ├── mytileset.png
    └── tileset.png`),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 3. CORE ARCHITECTURE
      // ═══════════════════════════════════════════════════════════════
      h1("3. Core Architecture"),

      // 3.1 Engine
      h2("3.1 Engine & Game Loop"),
      para("The Engine class (core/Engine.java) is the heart of the application. It implements Runnable and drives the entire game on a dedicated thread. Its responsibilities are:"),
      bullet("Running a fixed-timestep game loop targeting 60 FPS."),
      bullet("Computing delta time (dt) in seconds for frame-independent updates."),
      bullet("Clamping dt to a maximum of 0.25 seconds to prevent physics explosions after debugger pauses."),
      bullet("Calling update(dt) on the current Scene, then render()."),
      bullet("Managing the virtual canvas and double-buffered rendering."),

      bold("Pseudocode — Game Loop:"),
      ...codeBlock(
`WHILE running:
    elapsed = time since last frame (nanoseconds)
    dt = elapsed / 1,000,000,000   // convert to seconds
    IF dt > 0.25 THEN dt = 0.25   // clamp large spikes

    currentScene.update(dt)        // all systems process
    render()                       // draw to screen

    sleep for remaining frame time (target: 16.67ms per frame)`),

      bold("Actual Code — Engine.run():"),
      ...codeBlock(
`@Override
public void run() {
    final int FPS = 60;
    final long FRAME_NS = 1_000_000_000L / FPS;
    long lastTime = System.nanoTime();

    while (running) {
        long startTime = System.nanoTime();
        long elapsedNs = startTime - lastTime;
        lastTime = startTime;

        double dt = elapsedNs / 1_000_000_000.0;
        if (dt > 0.25) dt = 0.25;

        update(dt);
        render();

        long workTimeNs = System.nanoTime() - startTime;
        long remainingNs = FRAME_NS - workTimeNs;
        if (remainingNs > 0) {
            Thread.sleep(remainingNs / 1_000_000L,
                         (int) (remainingNs % 1_000_000L));
        }
    }
}`),

      bold("Rendering Pipeline:"),
      para("The render() method implements a two-stage pipeline:"),
      bullet("Stage 1: Draw the game world onto a small virtual canvas (BufferedImage). The resolution is defined by the PixelStyle enum (e.g., BIT_8 = 256×144 pixels)."),
      bullet("Stage 2: Scale the virtual canvas up to the full window size using nearest-neighbour interpolation, then present it via a BufferStrategy (double-buffered)."),
      para("This approach gives the game a crisp, retro pixel-art look regardless of the actual window size."),

      bold("Actual Code — Engine.render():"),
      ...codeBlock(
`private void render() {
    BufferStrategy bs = renderSurface.getBufferStrategy();
    if (bs == null) return;

    // Stage 1: draw world onto virtual canvas
    Graphics2D vg = virtualCanvas.createGraphics();
    vg.setColor(Color.BLACK);
    vg.fillRect(0, 0, pixelStyle.virtualWidth, pixelStyle.virtualHeight);
    currentScene.render(vg, pixelStyle.virtualWidth, pixelStyle.virtualHeight);
    vg.dispose();

    // Stage 2: scale up to real screen
    Graphics2D g = (Graphics2D) bs.getDrawGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                       RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    g.drawImage(virtualCanvas, 0, 0,
                renderSurface.getWidth(), renderSurface.getHeight(), null);
    g.dispose();
    bs.show();
}`),

      // 3.2 Window & GamePanel
      h2("3.2 Window & GamePanel"),
      para("Window (core/Window.java) is a thin wrapper around JFrame. It creates an undecorated, maximised fullscreen window and embeds a GamePanel inside it. When the Engine is set on the Window, it calls createBufferStrategy(3) on the GamePanel to initialise triple-buffering."),
      para("GamePanel (core/GamePanel.java) extends java.awt.Canvas. It serves purely as the render surface — it has no game logic. Its size matches the full screen, and it receives keyboard/mouse listeners from the InputManager."),

      // 3.3 Scene
      h2("3.3 Scene"),
      para("A Scene (core/Scene.java) is the container that ties everything together. It owns:"),
      bullet("An EntityManager — the registry of all entities in the scene."),
      bullet("One instance of every system — PlayerControlSystem, MovementSystem, ScriptSystem, PhysicsSystem, PickupSystem, TimeToLiveSystem, TileMapSystem, RenderingSystem, and LightingSystem."),
      para("The Scene defines the update and render order explicitly:"),

      bold("Update Order (called every frame):"),
      ...codeBlock(
`public void update(double dt) {
    playerControlSystem.update(entityManager, dt);  // 1. Read input, set velocities
    movementSystem.update(entityManager, dt);       // 2. Apply velocities to positions
    scriptSystem.update(entityManager, dt);         // 3. Run custom scripts
    physicsSystem.update(entityManager, dt);        // 4. Detect & resolve collisions
    pickupSystem.update(entityManager, dt);         // 5. Handle item pickups
    timeToLiveSystem.update(entityManager, dt);    // 6. Remove expired entities
}`),

      bold("Render Order:"),
      ...codeBlock(
`public void render(Graphics2D g, int screenW, int screenH) {
    tileMapSystem.render(entityManager, g, screenW, screenH);   // 1. Background tiles
    renderingSystem.render(entityManager, g, screenW, screenH);  // 2. Entity sprites
    lightingSystem.render(entityManager, g, screenW, screenH);   // 3. Darkness + lights
}`),

      para("This explicit ordering ensures predictable behaviour: input is read before movement, movement happens before collision detection, and tiles are drawn behind entity sprites."),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 4. ECS CORE
      // ═══════════════════════════════════════════════════════════════
      h1("4. Entity-Component-System (ECS)"),

      h2("4.1 Entity"),
      para("An Entity (entities/Entity.java) is the fundamental game object. It is not a base class to be extended — it is a final composition container. Each entity has:"),
      bullet("An integer ID, assigned by the EntityManager."),
      bullet("A String name, used for tagging and lookup (e.g., \"player\", \"camera\")."),
      bullet("A HashMap mapping component class types to component instances."),
      para("The entity enforces a one-component-per-type rule. Attempting to add a duplicate throws a DuplicateComponentException."),

      bold("Key Methods:"),
      ...codeBlock(
`// Add a component — returns 'this' for fluent chaining
public <T extends Component> Entity add(T component) {
    if (components.containsKey(component.getClass())) {
        throw new DuplicateComponentException("...");
    }
    components.put(component.getClass(), component);
    return this;
}

// Get a component by type — type-safe cast via Class.cast()
public <T extends Component> T get(Class<T> type) {
    return type.cast(components.get(type));
}

// Check if entity has a component type
public boolean has(Class<? extends Component> type) {
    return components.containsKey(type);
}

// Remove a component by type
public void remove(Class<? extends Component> type) {
    components.remove(type);
}`),

      bold("Usage Example:"),
      ...codeBlock(
`Entity player = new Entity(0, "player");
player.add(new Position())
      .add(new MovementValues())
      .add(new Sprite().setImageLink("hero.png"));

// Later, in a system:
Position pos = player.get(Position.class);
pos.x += 10;`),

      h2("4.2 Component (Base)"),
      para("Component (entities/components/Component.java) is an abstract class with an empty body. It exists solely as a type marker so that the Entity's generic map can constrain its values:"),
      ...codeBlock(
`public abstract class Component { }`),
      para("All components extend this class. Components are pure data — they contain fields but no behaviour logic (with the exception of convenience setters that return 'this' for fluent chaining)."),

      h2("4.3 EntityManager"),
      para("The EntityManager (entities/EntityManager.java) is responsible for entity lifecycle and lookup. It maintains:"),
      bullet("A List<Entity> for ordered iteration (systems iterate this list)."),
      bullet("A Map<Integer, Entity> for fast ID-based lookup."),
      bullet("A Map<String, Entity> for fast tag/name-based lookup (e.g., entityManager.getEntity(\"player\"))."),
      bullet("An auto-incrementing nextId counter."),

      bold("Key Methods:"),
      ...codeBlock(
`// Create a new entity with an auto-assigned ID
public Entity createEntity(String name) {
    Entity temp = new Entity(nextId, name);
    entities.add(temp);
    idLookup.put(nextId, temp);
    nextId++;
    return temp;
}

// Add an existing entity (also registers in tag lookup)
public void addEntity(Entity e) {
    this.entities.add(e);
    if (e.getEntityName() != null) {
        tagLookup.put(e.getEntityName(), e);
    }
}

// Look up by name tag
public Entity getEntity(String tag) {
    return tagLookup.get(tag);
}`),

      h2("4.4 GameSystem Interface"),
      para("GameSystem (entities/systems/GameSystem.java) is the contract all update-based systems implement:"),
      ...codeBlock(
`public interface GameSystem {
    void update(EntityManager entityManager, double dt);
}`),
      para("Each system receives the entire EntityManager and the delta time. The system is responsible for filtering entities by the components it requires. Rendering systems use a separate render() method signature that also takes a Graphics2D context and screen dimensions."),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 5. COMPONENTS IN DEPTH
      // ═══════════════════════════════════════════════════════════════
      h1("5. Components — In Depth"),
      para("This section describes every component in the engine, what data it holds, and how systems use it."),

      // 5.1 Position
      h2("5.1 Transform: Position"),
      para("Package: entities.components.transform"),
      para("Position stores an entity's location and orientation in 2D world space."),
      ...codeBlock(
`public class Position extends Component {
    public double x = 0.0;        // world X coordinate
    public double y = 0.0;        // world Y coordinate
    public double rotation = 0.0; // rotation in radians
}`),
      para("Position represents the top-left corner of the entity's visual (sprite). Systems that need the centre (for rotation, collision, or facing calculations) compute it by adding half the sprite's width/height. Rotation is measured in radians and applied around the sprite's centre during rendering."),
      bold("Used By: MovementSystem, PlayerControlSystem, PhysicsSystem, RenderingSystem, TileMapSystem, ScriptSystem"),

      // 5.2 Parent/Child
      h2("5.2 Transform: ParentEntity & ChildEntity"),
      para("Package: entities.components.transform"),
      para("These components establish parent-child relationships between entities. ParentEntity stores a reference to a parent entity, and ChildEntity stores a reference to a child. These are used in custom scripts to synchronise positions — for example, making an enemy's detection radius follow the enemy's position."),
      ...codeBlock(
`public class ParentEntity extends Component {
    public Entity parentEntity;
    public ParentEntity setParentEntity(Entity e) {
        this.parentEntity = e;
        return this;
    }
}`),

      // 5.3 MovementValues
      h2("5.3 Movement: MovementValues"),
      para("Package: entities.components.movement"),
      para("MovementValues stores the velocity and speed of an entity. It is pure data — the actual movement is applied by the MovementSystem."),
      ...codeBlock(
`public class MovementValues extends Component {
    public double speed = 50.0;      // movement speed (pixels/second)
    public double velocityX = 0.0;   // current X velocity
    public double velocityY = 0.0;   // current Y velocity
}`),
      para("The PlayerControlSystem writes to velocityX/Y each frame based on input. The MovementSystem then reads these values and updates Position accordingly."),

      // 5.4 InputState & PlayerControlled
      h2("5.4 Input: InputState & PlayerControlled"),
      para("Package: entities.components.input"),
      para("PlayerControlled is a marker/flag component — it has no fields. Its presence on an entity tells the PlayerControlSystem that this entity should respond to player input."),
      para("InputState stores the current input state for a player-controlled entity:"),
      ...codeBlock(
`public class InputState extends Component {
    // Keyboard state (set by PlayerControlSystem each frame)
    public boolean movingUp, movingDown, movingLeft, movingRight;

    // Mouse position in world coordinates
    public int mouseX, mouseY;

    // Click-to-move target
    public int targetX, targetY;
    public boolean isMovingToTarget = false;

    // Movement mode flags
    public boolean clickToMove = false;     // click to set destination
    public boolean keyboardToMove = false;  // WASD direct control
}`),
      para("The two movement modes can coexist. Click-to-move sets a target point; the entity moves toward it until within 5 pixels. Keyboard-to-move provides immediate directional control with optional camera-relative rotation."),

      // 5.5 Collision
      h2("5.5 Physics: Collision"),
      para("Package: entities.components.physics"),
      para("Collision defines the shape, size, and filtering rules for collision detection:"),
      ...codeBlock(
`public class Collision extends Component {
    public enum Shape { BOX, CIRCLE }

    public Shape shape = Shape.BOX;

    // Box dimensions
    public double width = 8.0;
    public double height = 8.0;

    // Circle radius
    public double radius = 24.0;

    // If false, collision is detected but not physically resolved
    public boolean solid = true;

    // Offset from auto-centred position
    public double offsetX = 0.0;
    public double offsetY = 0.0;

    // Bit-flag filtering
    public int layer = 1;   // what this entity IS (its layer)
    public int mask = -1;   // what layers it COLLIDES WITH (-1 = all)

    // Populated each frame by PhysicsSystem
    public List<Entity> collidedWith = new ArrayList<>();
}`),
      para("The layer/mask system uses bitwise AND for filtering. Two entities only test for collision if (A.layer & B.mask) != 0 OR (B.layer & A.mask) != 0. This allows efficient categorical filtering (e.g., player bullets only hit enemies, not other players)."),
      para("The collidedWith list is cleared at the start of each physics frame and populated during detection. Other systems (like PickupSystem or custom scripts) can read this list to react to collisions."),

      // 5.6 RigidBody
      h2("5.6 Physics: RigidBody"),
      para("Package: entities.components.physics"),
      ...codeBlock(
`public class RigidBody extends Component {
    public boolean movable = true;

    public RigidBody setMovable(boolean movable) {
        this.movable = movable;
        return this;
    }
}`),
      para("RigidBody marks an entity as participating in physics resolution (push-apart). The movable flag controls whether the entity can be displaced during collision resolution. Walls use movable = false so they never get pushed, while the player uses movable = true. If both colliding entities are movable, they share the displacement equally (each pushed half the penetration distance)."),
      para("An entity with Collision but no RigidBody will still detect collisions (and populate collidedWith), but will not be physically pushed apart. This is used for triggers and sensors."),

      // 5.7 Pickup
      h2("5.7 Physics: Pickup"),
      para("Package: entities.components.physics"),
      ...codeBlock(
`public class Pickup extends Component {
    public String type = "coin";       // item type identifier
    public boolean collected = false;  // set true when picked up
}`),
      para("Pickup marks an entity as a collectible item. The PickupSystem checks collision events: when a PlayerControlled entity collides with a Pickup entity, the pickup is marked as collected and removed from the scene. The Collision on a pickup typically has solid = false so there is no physical push-apart."),

      // 5.8 Sprite
      h2("5.8 Rendering: Sprite"),
      para("Package: entities.components.rendering"),
      ...codeBlock(
`public class Sprite extends Component {
    public BufferedImage image;

    public Sprite setImageLink(String imageLink) {
        imageLink = "sprites/" + imageLink;
        image = ImageIO.read(getClass().getClassLoader()
                    .getResource(imageLink));
        return this;
    }
}`),
      para("Sprite holds the visual representation of an entity — a BufferedImage loaded from the classpath (res/sprites/ directory). The setImageLink() convenience method prepends the sprites folder path. The image dimensions are also used by other systems to calculate sprite centres for rotation, collision centring, and camera following."),

      // 5.9 Layer
      h2("5.9 Rendering: Layer"),
      para("Package: entities.components.rendering"),
      ...codeBlock(
`public class Layer extends Component {
    public int layerLevel = 0;
    public Layer setLayerLevel(int layerLevel) {
        this.layerLevel = layerLevel;
        return this;
    }
}`),
      para("Layer controls the rendering order of entities. The RenderingSystem sorts all entities by layerLevel before drawing — lower values are drawn first (behind), higher values are drawn on top. Entities without a Layer component default to level 0."),

      // 5.10 Camera
      h2("5.10 Rendering: Camera"),
      para("Package: entities.components.rendering"),
      ...codeBlock(
`public class Camera extends Component {
    public Entity target;          // entity to follow

    public double userOffsetX;     // user-defined offset from target
    public double userOffsetY;

    public double offsetX;         // computed world offset (set by RenderingSystem)
    public double offsetY;
    public double rotation;        // camera rotation in radians
    public double zoom = 1.0;      // zoom level (> 1 = zoomed in)

    public Camera setTarget(Entity target) {
        this.target = target;
        return this;
    }
}`),
      para("Camera is attached to a dedicated camera entity (not the player). It follows a target entity, centering the view on the target's sprite centre. The user offset allows shifting the view (e.g., looking ahead of the player). The computed offsetX/Y values are used by both the TileMapSystem and RenderingSystem to translate the world. Zoom and rotation are applied as affine transforms around the screen centre."),

      // 5.11 FaceMouse & FaceEntity
      h2("5.11 Rendering: FaceMouse & FaceEntity"),
      para("Package: entities.components.rendering"),
      para("FaceMouse causes an entity to rotate to face the mouse cursor:"),
      ...codeBlock(
`public class FaceMouse extends Component {
    public boolean faceingMouse = true; // toggle on/off
}`),
      para("FaceEntity causes an entity to rotate to face another specific entity:"),
      ...codeBlock(
`public class FaceEntity extends Component {
    Entity toFace;
    public boolean faceEntity = true;

    public FaceEntity(Entity e) { this.toFace = e; }
}`),
      para("Both are processed by the PlayerControlSystem. The rotation is calculated using Math.atan2() and written to the entity's Position.rotation field. This rotation is then applied during rendering."),

      // 5.12 RotateViewToMouse
      h2("5.12 Rendering: RotateViewToMouse"),
      para("Package: entities.components.rendering"),
      para("RotateViewToMouse is attached to the camera entity. When enabled, it locks the mouse cursor and uses mouse delta movement to rotate the camera. This creates an FPS-style look-around effect. The sensitivity field controls how fast the camera rotates per pixel of mouse movement."),

      // 5.13 Light
      h2("5.13 Rendering: Light"),
      para("Package: entities.components.rendering"),
      para("Light marks an entity as a point light source. The LightingSystem renders an ambient darkness overlay and subtracts radial gradients where Light components exist."),
      ...codeBlock(
`public class Light extends Component {
    public double radius = 32;       // light reach in world units
    public double intensity = 1.0;   // 0.0–1.0, how much darkness to erase
    public Color color = Color.WHITE; // tint of the light
}`),
      para("The light is centred on the entity's sprite (if it has one) or its Position. Radius is in world units and scales with camera zoom. Intensity controls the alpha of the gradient — 1.0 fully erases darkness at the centre, 0.5 partially erases it. Color tints the gradient for effects like warm torchlight or cold blue."),
      bold("Used By: LightingSystem"),

      // 5.14 TileMap
      h2("5.14 World: TileMap"),
      para("Package: entities.components.world"),
      ...codeBlock(
`public class TileMap extends Component {
    public int tileSize;               // pixel size of one tile (e.g. 8)
    public int[][] map;                // 2D grid of tile indices
    public int mapWidth, mapHeight;    // grid dimensions (in tiles)
    public String tilesetPath;         // path to tileset image
    public String mapPath;             // path to .map file

    // populated by TileMapSystem (via TileMapUtils)
    public BufferedImage tileset;      // the loaded tileset sprite sheet
    public BufferedImage[] tileCache;  // pre-sliced tile images
    public boolean loaded = false;     // true once loading is complete
}`),
      para("TileMap is a pure data component — it holds the configuration and state for a tile-based world but contains no loading or rendering logic. The fluent setters setTileset() and setMap() store file path strings only; they do not perform any I/O. The actual loading of the tileset image, tile slicing, and map file parsing is handled externally by TileMapUtils (see Section 11.1), which is called either explicitly during scene setup or lazily by the TileMapSystem on first render."),
      para("This separation follows the ECS principle that components are plain data containers. The TileMap component stores two categories of data:"),
      bullet("Configuration — tileSize, tilesetPath, and mapPath are set by the user when creating the component. These define what the tilemap should look like and where its data comes from."),
      bullet("Runtime state — tileset, tileCache, map, mapWidth, mapHeight, and loaded are populated by the system/utility at load time. The tileCache is an array of pre-sliced BufferedImages indexed by tile ID for O(1) lookup during rendering."),
      para("The getTileImage(int tileIndex) method is a simple data accessor that returns the cached tile image for a given index, or null if the index is out of range or the cache has not been built yet."),

      bold("TileEntitySpawner — Functional Interface:"),
      para("TileEntitySpawner is a functional interface in the same package that defines a callback for creating entities from tile data:"),
      ...codeBlock(
`@FunctionalInterface
public interface TileEntitySpawner {
    Entity spawnEntity(int tileIndex, int worldX, int worldY);
}`),
      para("This interface is used by TileMapUtils.spawnEntities() (see Section 11.1) to let the caller decide which tiles produce entities and what components those entities should have. For example, returning a wall Entity for tile index 1 and null for everything else."),

      // 5.15 Script & ScriptComponent
      h2("5.15 Scripting: Script & ScriptComponent"),
      para("Package: entities.components"),
      para("Script is a functional interface that allows custom per-entity behaviour via lambdas:"),
      ...codeBlock(
`@FunctionalInterface
public interface Script {
    void update(Entity self, EntityManager entityManager, double dt);
}`),
      para("ScriptComponent wraps a Script and is attached to an entity as a component:"),
      ...codeBlock(
`public class ScriptComponent extends Component {
    private final Script script;

    public ScriptComponent(Script script) {
        this.script = script;
    }

    public void update(Entity self, EntityManager entityManager, double dt) {
        script.update(self, entityManager, dt);
    }
}`),
      para("This is the engine's extensibility mechanism. Any behaviour that doesn't warrant a full system can be implemented as a lambda. For example, the enemy border entity uses a script to follow its parent entity and react to collisions with the player."),

      bold("Example — Enemy border script:"),
      ...codeBlock(
`new ScriptComponent((self, entityManager, dt) -> {
    // Sync position to parent entity
    Position thisP = self.get(Position.class);
    Entity parent = self.get(ParentEntity.class).parentEntity;
    Position parentPos = parent.get(Position.class);
    thisP.x = parentPos.x;
    thisP.y = parentPos.y;

    // Centre on parent's sprite
    if (parent.has(Sprite.class) && parent.get(Sprite.class).image != null) {
        thisP.x += parent.get(Sprite.class).image.getWidth() / 2.0;
        thisP.y += parent.get(Sprite.class).image.getHeight() / 2.0;
    }

    // React to collision with player
    Collision col = self.get(Collision.class);
    Entity player = entityManager.getEntity("player");
    if (col.collidedWith.contains(player)) {
        parent.get(Position.class).x += 1; // push parent away
    }
})`),

      // 5.16 AudioSource
      h2("5.16 Audio: AudioSource"),
      para("Package: entities.components.audio"),
      para("AudioSource attaches audio playback to an entity."),
      ...codeBlock(
`public class AudioSource extends Component {
    public boolean loop = false;     // whether the clip loops continuously
    public boolean play = false;     // set to true to trigger playback
    public String filePath = "";     // path to the audio resource
    public float volume = 1.0f;     // playback volume, clamped 0.0–1.0
}`),
      para("Volume is clamped in the setter — values above 1.0 are capped to 1.0, values below 0.0 are capped to 0.0. All fields have fluent setters for chaining."),

      // 5.17 Stats
      h2("5.17 RPG: Stats"),
      para("Package: entities.components.rpgsystem"),
      para("Stats is a placeholder component for RPG mechanics. It stores health, attack, defence, level, and experience. No system currently processes it — it is reserved for future gameplay implementation."),

      // 5.18 TimeToLive
      h2("5.18 Util: TimeToLive"),
      para("Package: entities.components.util"),
      para("TimeToLive marks an entity for automatic removal after a duration. The TimeToLiveSystem decrements ttl each frame and removes the entity when it reaches zero."),
      ...codeBlock(
`public class TimeToLive extends Component {
    public double ttl = 4; // time to live in seconds
}`),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 6. SYSTEMS IN DEPTH
      // ═══════════════════════════════════════════════════════════════
      h1("6. Systems — In Depth"),
      para("Systems contain all behaviour logic. Each system iterates entities, checks for required components, and processes them. This section explains each system's algorithm in both pseudocode and actual code."),

      // 6.1 PlayerControlSystem
      h2("6.1 PlayerControlSystem"),
      para("File: entities/systems/PlayerControlSystem.java"),
      para("This is the most complex system. It handles three responsibilities:"),
      bullet("Reading hardware input (keyboard/mouse) and writing it to InputState components."),
      bullet("Converting input into velocity on MovementValues."),
      bullet("Rotating entities to face the mouse or another entity (FaceMouse, FaceEntity)."),

      bold("Pseudocode:"),
      ...codeBlock(
`// Phase 1: Find camera for coordinate transforms
FOR each entity with Camera:
    Record camera offset and rotation
    IF camera has RotateViewToMouse:
        Lock mouse, read delta, update camera rotation

// Phase 2: Process player-controlled entities
FOR each entity with PlayerControlled + InputState + MovementValues + Position:
    Copy keyboard state (WASD) to InputState
    Copy mouse world position to InputState

    Reset velocity to (0, 0)

    IF mouse clicked AND clickToMove enabled:
        Set movement target to click position
        Flag isMovingToTarget = true

    IF isMovingToTarget:
        direction = target - entity centre
        IF distance < 5 pixels: stop (arrived)
        ELSE: velocity = normalize(direction) * speed

    IF keyboardToMove enabled:
        velocity = WASD input * speed
        Rotate velocity by camera angle (so W = forward)

// Phase 3: Entity facing
FOR each entity with FaceMouse:
    rotation = atan2(mouseX - centreX, mouseY - centreY)

FOR each entity with FaceEntity:
    rotation = atan2(otherX - centreX, otherY - centreY)`),

      bold("Click-to-Move — Actual Code:"),
      ...codeBlock(
`if (im.getMouse().clicked && input.clickToMove) {
    input.targetX = input.mouseX;
    input.targetY = input.mouseY;
    input.isMovingToTarget = true;
    im.getMouse().clicked = false;
}

if (input.isMovingToTarget) {
    double centerX = pos.x;
    double centerY = pos.y;
    if (e.has(Sprite.class) && e.get(Sprite.class).image != null) {
        centerX += e.get(Sprite.class).image.getWidth() / 2.0;
        centerY += e.get(Sprite.class).image.getHeight() / 2.0;
    }
    double dx = input.targetX - centerX;
    double dy = input.targetY - centerY;
    double dist = Math.sqrt(dx * dx + dy * dy);

    if (dist < 5) {
        input.isMovingToTarget = false;
    } else {
        mov.velocityX = (dx / dist) * mov.speed;
        mov.velocityY = (dy / dist) * mov.speed;
    }
}`),

      bold("Camera-Relative Keyboard Movement — Actual Code:"),
      ...codeBlock(
`if (input.keyboardToMove) {
    double inputX = 0, inputY = 0;
    if (input.movingUp)    inputY -= mov.speed;
    if (input.movingDown)  inputY += mov.speed;
    if (input.movingLeft)  inputX -= mov.speed;
    if (input.movingRight) inputX += mov.speed;

    // Rotate by camera angle so W always = forward
    double sin = Math.sin(camRotation);
    double cos = Math.cos(camRotation);
    mov.velocityX = inputX * cos - inputY * sin;
    mov.velocityY = inputX * sin + inputY * cos;
}`),

      // 6.2 MovementSystem
      h2("6.2 MovementSystem"),
      para("File: entities/systems/MovementSystem.java"),
      para("The simplest system. It applies velocity to position using delta time for frame-independent movement."),

      bold("Pseudocode:"),
      ...codeBlock(
`FOR each entity with Position + MovementValues:
    position.x += velocityX * dt
    position.y += velocityY * dt`),

      bold("Actual Code:"),
      ...codeBlock(
`@Override
public void update(EntityManager entityManager, double dt) {
    for (Entity e : entityManager.getEntities()) {
        if (e.has(Position.class) && e.has(MovementValues.class)) {
            Position pos = e.get(Position.class);
            MovementValues mov = e.get(MovementValues.class);
            pos.x += mov.velocityX * dt;
            pos.y += mov.velocityY * dt;
        }
    }
}`),

      // 6.3 ScriptSystem
      h2("6.3 ScriptSystem"),
      para("File: entities/systems/ScriptSystem.java"),
      para("Iterates all entities with a ScriptComponent and calls their update method. This is the bridge between the ECS framework and custom per-entity behaviours."),

      bold("Pseudocode & Actual Code:"),
      ...codeBlock(
`FOR each entity with ScriptComponent:
    scriptComponent.update(entity, entityManager, dt)

// Which delegates to the lambda:
//   script.update(self, entityManager, dt)`),

      // 6.4 PhysicsSystem
      h2("6.4 PhysicsSystem"),
      para("File: entities/systems/PhysicsSystem.java"),
      para("The PhysicsSystem handles collision detection and resolution. It supports three collision shape combinations: Circle-Circle, OBB-OBB (Oriented Bounding Box), and OBB-Circle."),

      bold("High-Level Pseudocode:"),
      ...codeBlock(
`// Step 1: Collect all collidable entities and clear previous frame data
collidables = []
FOR each entity with Position + Collision:
    Clear collidedWith list
    Add to collidables

// Step 2: Broad check — N×N pairwise (skipping self-pairs)
FOR each pair (A, B) in collidables:
    // Layer/mask filter
    IF (A.layer & B.mask) == 0 AND (B.layer & A.mask) == 0:
        SKIP

    result = checkCollision(A, B)  // returns {normalX, normalY, penetration} or null

    IF result != null:
        A.collidedWith.add(B)
        B.collidedWith.add(A)

        // Only resolve if both have RigidBody and both are solid
        IF A.has(RigidBody) AND B.has(RigidBody) AND A.solid AND B.solid:
            resolveCollision(A, B, result)`),

      bold("Collision Detection — Circle vs Circle:"),
      ...codeBlock(
`// Pseudocode:
distance = sqrt((centreA.x - centreB.x)² + (centreA.y - centreB.y)²)
IF distance >= radiusA + radiusB: NO COLLISION
penetration = (radiusA + radiusB) - distance
normal = (centreA - centreB) / distance`),

      bold("Collision Detection — OBB vs OBB (Separating Axis Theorem):"),
      ...codeBlock(
`// Uses SAT with 4 test axes: 2 from each box's orientation
FOR each of the 4 axes:
    Project all corners of both boxes onto the axis
    overlap = min(maxA, maxB) - max(minA, minB)
    IF overlap <= 0: NO COLLISION (separating axis found)
    Track the axis with minimum overlap (minimum penetration axis)

// Normal points from B to A
IF dot(minAxis, centreA - centreB) < 0: flip minAxis`),

      bold("Collision Detection — OBB vs Circle:"),
      ...codeBlock(
`// Transform circle centre into box's local (unrotated) space
localCircle = rotate(circleCentre - boxCentre, -boxRotation)

// Clamp to box extents to find closest point on box
closest.x = clamp(localCircle.x, -halfWidth, halfWidth)
closest.y = clamp(localCircle.y, -halfHeight, halfHeight)

distance = |localCircle - closest|
IF distance >= circleRadius: NO COLLISION

// Special case: circle centre inside box
IF distance == 0: push along shortest axis

// Rotate normal back to world space`),

      bold("Collision Resolution:"),
      ...codeBlock(
`private void resolveCollision(Entity a, Entity b, double[] result) {
    double nx = result[0], ny = result[1], penetration = result[2];
    boolean aMovable = a.get(RigidBody.class).movable;
    boolean bMovable = b.get(RigidBody.class).movable;

    if (!aMovable && !bMovable) return; // neither can move

    if (aMovable && bMovable) {
        double push = penetration / 2;    // split equally
        posA.x += nx * push;  posA.y += ny * push;
        posB.x -= nx * push;  posB.y -= ny * push;
    } else if (aMovable) {
        posA.x += nx * penetration;       // only A moves
        posA.y += ny * penetration;
    } else {
        posB.x -= nx * penetration;       // only B moves
        posB.y -= ny * penetration;
    }
}`),

      // 6.5 PickupSystem
      h2("6.5 PickupSystem"),
      para("File: entities/systems/PickupSystem.java"),
      para("Processes collision events to handle item collection."),

      bold("Pseudocode:"),
      ...codeBlock(
`toRemove = []
FOR each entity with Collision + PlayerControlled:
    FOR each entity in collidedWith list:
        IF other has Pickup component AND not yet collected:
            Mark as collected
            Print "{entityName} picked up {type}"
            Schedule for removal

FOR each entity in toRemove:
    Remove from EntityManager`),

      bold("Actual Code:"),
      ...codeBlock(
`@Override
public void update(EntityManager entityManager, double dt) {
    List<Entity> toRemove = new ArrayList<>();
    for (Entity e : entityManager.getEntities()) {
        if (!e.has(Collision.class) && !e.has(PlayerControlled.class)) continue;
        Collision col = e.get(Collision.class);
        for (Entity other : col.collidedWith) {
            if (other.has(Pickup.class)) {
                Pickup pickup = other.get(Pickup.class);
                if (!pickup.collected) {
                    pickup.collected = true;
                    System.out.println(e.getEntityName() + " picked up " + pickup.type);
                    toRemove.add(other);
                }
            }
        }
    }
    for (Entity e : toRemove) entityManager.removeEntity(e);
}`),

      // 6.6 TimeToLiveSystem
      h2("6.6 TimeToLiveSystem"),
      para("File: entities/systems/TimeToLiveSystem.java"),
      para("Decrements the ttl field on every entity with a TimeToLive component. When ttl reaches zero or below, the entity is removed from the EntityManager."),

      bold("Pseudocode:"),
      ...codeBlock(
`toRemove = []
FOR each entity with TimeToLive:
    ttl -= dt
    IF ttl <= 0:
        Schedule for removal

FOR each entity in toRemove:
    Remove from EntityManager`),

      // 6.7 TileMapSystem
      h2("6.7 TileMapSystem"),
      para("File: entities/systems/TileMapSystem.java"),
      para("Renders the tile-based world. It has two responsibilities: lazy-loading tilemap data on first encounter, and drawing visible tiles with camera-aware frustum culling."),
      para("When TileMapSystem encounters a TileMap component that has not yet been loaded (loaded == false), it delegates to TileMapUtils.load() which parses the map file and slices the tileset image into individual cached tiles. This lazy-loading approach means tilemaps can be created with just path strings during scene setup — the heavy I/O only happens once, on the first frame that needs the data. If TileMapUtils.spawnEntities() was called during scene setup, the map data will already be loaded and this step is skipped."),
      para("For rendering, the system applies the full camera transform (zoom, rotation, translation) and then calculates which tiles fall within the visible viewport. It accounts for both zoom (visible area shrinks when zoomed in) and rotation (the axis-aligned bounding box of a rotated rectangle is larger than the rectangle itself). Only tiles within this expanded AABB are drawn, which is critical for performance on large maps."),

      bold("Pseudocode:"),
      ...codeBlock(
`// Apply camera transform (zoom, rotation, translation)
g.translate(screenCentre)
g.scale(camZoom)
g.rotate(-camRotation)
g.translate(-screenCentre)
g.translate(-camOffset)

FOR each entity with TileMap + Position:
    IF NOT tilemap.loaded:
        TileMapUtils.load(tilemap)  // lazy-load tileset + map data

    // Calculate visible tile range (frustum culling with rotation)
    visibleW = screenW / camZoom
    visibleH = screenH / camZoom
    aabbHalfW = (visibleW/2) * |cos(rot)| + (visibleH/2) * |sin(rot)|
    aabbHalfH = (visibleW/2) * |sin(rot)| + (visibleH/2) * |cos(rot)|

    startCol = max(0, (viewLeft - pos.x) / tileSize)
    startRow = max(0, (viewTop  - pos.y) / tileSize)
    endCol   = min(mapWidth,  (viewRight  - pos.x) / tileSize + 2)
    endRow   = min(mapHeight, (viewBottom - pos.y) / tileSize + 2)

    FOR row = startRow TO endRow:
        FOR col = startCol TO endCol:
            tileIndex = map[row][col]
            IF tileIndex < 0: SKIP (empty)
            tileImage = tileCache[tileIndex]
            Draw tileImage at (pos.x + col * tileSize, pos.y + row * tileSize)

// Restore original transform`),
      para("The entity's Position component determines where the tilemap is drawn in world space. This means moving the tilemap entity's position moves the entire visual grid. When using TileMapUtils.spawnEntities() to create collision entities from the map, the same offset must be passed as the origin so that the spawned entities align with the rendered tiles."),

      // 6.8 RenderingSystem
      h2("6.8 RenderingSystem"),
      para("File: entities/systems/RenderingSystem.java"),
      para("Renders all entity sprites with camera transformation, layer sorting, and per-entity rotation."),

      bold("Pseudocode:"),
      ...codeBlock(
`// Sort entities by Layer level (ascending = back to front)
entities.sort(by layerLevel, default 0)

// Find camera entity, compute world offset from target
FOR each entity with Camera:
    camX = target.centreX - screenW/2 + rotatedUserOffset.x
    camY = target.centreY - screenH/2 + rotatedUserOffset.y

// Apply camera transform
g.translate(screenCentre)
g.scale(camZoom)
g.rotate(-camRotation)
g.translate(-screenCentre)
g.translate(-camOffset)

// Draw each entity
FOR each entity with Sprite + Position:
    Save transform
    Rotate around sprite centre by entity.rotation
    Draw sprite at (pos.x, pos.y)
    Restore transform

// Restore base transform`),

      bold("Actual Code — Sprite Rendering with Rotation:"),
      ...codeBlock(
`for (Entity e : MyList) {
    if (!e.has(Sprite.class) || !e.has(Position.class)) continue;
    Position pos = e.get(Position.class);
    Sprite spr = e.get(Sprite.class);

    AffineTransform old = g.getTransform();
    double centerX = pos.x + spr.image.getWidth() / 2.0;
    double centerY = pos.y + spr.image.getHeight() / 2.0;

    g.rotate(pos.rotation, centerX, centerY);
    g.translate(pos.x, pos.y);
    g.drawImage(spr.image, 0, 0, null);
    g.translate(-pos.x, -pos.y);
    g.setTransform(old);
}`),

      // 6.9 LightingSystem
      h2("6.9 LightingSystem"),
      para("File: entities/systems/LightingSystem.java"),
      para("Renders dynamic lighting as a post-process overlay. The system creates a screen-sized darkness image, fills it with ambient darkness, then subtracts radial gradients at each Light entity's position."),

      bold("Configuration:"),
      ...codeBlock(
`scene.getLightingSystem().setEnabled(true);       // toggle on/off
scene.getLightingSystem().setAmbientDarkness(200); // 0 = no darkness, 255 = pitch black`),

      bold("Pseudocode:"),
      ...codeBlock(
`IF NOT enabled OR ambientDarkness <= 0: RETURN

// Create darkness overlay at virtual canvas size
lightMap = new BufferedImage(screenW, screenH, ARGB)
Fill lightMap with Color(0, 0, 0, ambientDarkness)

// Find camera transform (mirrors RenderingSystem)
Build camTransform from cam offset, zoom, rotation

// Erase darkness where lights are (DstOut composite)
FOR each entity with Light + Position:
    Transform world position to screen position via camTransform
    Scale radius by camZoom
    Paint radial gradient (centre = light colour at intensity alpha, edge = transparent)

// Draw lightMap over the scene (identity transform — already screen space)`),
      para("The system uses AlphaComposite.DstOut to erase darkness — this means the light gradient subtracts from the overlay rather than adding to the scene. The colour of the light tints the gradient for effects like warm torchlight (orange) or cold moonlight (blue). Intensity controls how much darkness is erased at the centre: 1.0 fully removes it, 0.5 leaves it half-dark."),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 7. INPUT MANAGEMENT
      // ═══════════════════════════════════════════════════════════════
      h1("7. Input Management"),
      para("Input is handled by three classes in the input package:"),

      bold("InputManager"),
      para("A container that holds references to the Keyboard and Mouse instances. It also stores a reference to the AWT component for mouse locking. The Engine creates it and attaches the listeners to the GamePanel."),

      bold("Keyboard"),
      para("Implements KeyListener. Tracks the pressed state of WASD keys and additional keys (T for engine stop). Key states are stored as public booleans (e.g., W_key_pressed) and updated on keyPressed/keyReleased events."),

      bold("Mouse"),
      para("Implements MouseListener and MouseMotionListener. Tracks:"),
      bullet("Current position (x, y) in virtual canvas coordinates — scaled from real screen coordinates using the scale factor."),
      bullet("Click events (clicked flag, consumed by PlayerControlSystem)."),
      bullet("Locked mode — hides the cursor and tracks delta movement for RotateViewToMouse. Uses a Robot to warp the cursor back to screen centre each frame."),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 8. EXECUTION ORDER & DATA FLOW
      // ═══════════════════════════════════════════════════════════════
      h1("8. System Execution Order & Data Flow"),
      para("Understanding the order in which systems execute and how data flows between them is critical to understanding the engine's behaviour. Here is the complete per-frame pipeline:"),

      ...codeBlock(
`┌─────────────────────────────────────────────────────────────────┐
│                        FRAME START                              │
│  Engine computes dt from elapsed nanoseconds                    │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│  1. PlayerControlSystem                                         │
│     READS:  Keyboard state, Mouse state, Camera offset/rotation │
│     WRITES: InputState, MovementValues.velocity, Position.rot   │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│  2. MovementSystem                                              │
│     READS:  MovementValues.velocity, dt                         │
│     WRITES: Position.x, Position.y                              │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│  3. ScriptSystem                                                │
│     READS:  Any component (via entity access)                   │
│     WRITES: Any component (scripts have full access)            │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│  4. PhysicsSystem                                               │
│     READS:  Position, Collision, RigidBody, Sprite              │
│     WRITES: Collision.collidedWith, Position (resolution)       │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│  5. PickupSystem                                                │
│     READS:  Collision.collidedWith, PlayerControlled, Pickup    │
│     WRITES: Pickup.collected, removes entities                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│  6. TimeToLiveSystem                                            │
│     READS:  TimeToLive.ttl, dt                                  │
│     WRITES: TimeToLive.ttl, removes expired entities            │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│  RENDER PHASE                                                   │
│  7. TileMapSystem   → lazy-loads + draws background tiles       │
│  8. RenderingSystem → draws entity sprites (sorted by Layer)    │
│  9. LightingSystem  → ambient darkness + point light subtraction│
│     READS: Position, Sprite, Layer, Camera, TileMap, Light      │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│  Engine scales virtual canvas to screen, presents buffer        │
│                        FRAME END                                │
└─────────────────────────────────────────────────────────────────┘`),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 9. CREATOR — COMPONENT FACTORY
      // ═══════════════════════════════════════════════════════════════
      h1("9. Creator — Component Factory"),
      para("File: entities/components/Creator.java"),
      para("Creator is a static factory class that provides a factory method for every component in the engine. Its purpose is to improve iteration speed: typing Creator. in an IDE shows every available component via autocomplete, making it easy to discover and attach components without reading the API."),

      bold("Usage:"),
      ...codeBlock(
`// Instead of:
player.add(new Position());
player.add(new MovementValues());
player.add(new Sprite().setImageLink("player.png"));

// You can use:
player.add(Creator.position());
player.add(Creator.movementValues());
player.add(Creator.sprite().setImageLink("player.png"));`),
      para("Both approaches are equivalent — Creator methods simply return new instances. The factory methods still return the concrete component type, so fluent setter chaining works as normal. Creator is entirely optional; using new directly is equally valid."),

      bold("Available Methods:"),
      ...codeBlock(
`// Transform
Creator.position()              → new Position()
Creator.childEntity()           → new ChildEntity()
Creator.parentEntity()          → new ParentEntity()

// Movement
Creator.movementValues()        → new MovementValues()

// Physics
Creator.rigidBody()             → new RigidBody()
Creator.collision()             → new Collision()
Creator.pickup()                → new Pickup()

// Rendering
Creator.sprite()                → new Sprite()
Creator.layer()                 → new Layer()
Creator.animation()             → new Animation()
Creator.camera()                → new Camera()
Creator.light()                 → new Light()
Creator.faceMouse()             → new FaceMouse()
Creator.faceEntity(Entity e)    → new FaceEntity(e)
Creator.rotateViewToMouse()     → new RotateViewToMouse()

// Input
Creator.playerControlled()      → new PlayerControlled()
Creator.inputState()            → new InputState()

// Audio
Creator.audioSource()           → new AudioSource()

// World
Creator.tileMap(int tileSize)   → new TileMap(tileSize)

// RPG
Creator.stats()                 → new Stats()

// Script
Creator.script(Script s)        → new ScriptComponent(s)

// Util
Creator.timeToLive()            → new TimeToLive()`),

      pageBreak(),

      // ═══════════════════════════════════════════════════════════════
      // 10. EXAMPLE SCENE
      // ═══════════════════════════════════════════════════════════════
      h1("10. Example: Building a Scene"),
      para("The Main class (core/Main.java) demonstrates how to wire up a complete scene. Here is a walkthrough of the test scene:"),

      bold("Step 1 — Create the Engine and Window:"),
      ...codeBlock(
`Window w = new Window("Hello");
Engine e = new Engine(w.getRenderSurface(), PixelStyle.BIT_8);
e.setScene(myScene(e.getInputManager()));
w.setEngine(e);
e.start();`),

      bold("Step 2 — Create a Player Entity:"),
      ...codeBlock(
`Entity player = new Entity(0, "player");
player.add(Creator.position());                              // world position
player.add(Creator.movementValues());                        // speed + velocity
player.add(Creator.inputState().setKeyboardToMove(true));    // keyboard control
player.add(Creator.playerControlled());                      // flag: responds to input
player.add(Creator.collision());                             // 8×8 box collider
player.add(Creator.rigidBody());                             // physics: movable
player.add(Creator.faceMouse());                             // rotate toward cursor
player.add(Creator.layer().setLayerLevel(1));                // draw above enemies
player.add(Creator.sprite().setImageLink("blue8bitsqr.png")); // visual
player.add(Creator.light().setRadius(18).setIntensity(1.0)); // point light`),
      para("This single entity declaration gives the player: keyboard movement, mouse-facing rotation, collision detection and resolution, sprite rendering on layer 1, and a point light."),

      bold("Step 3 — Create a Camera:"),
      ...codeBlock(
`Entity camera = new Entity(0, "camera");
Camera cam = new Camera().setTarget(player);
cam.zoom = 1.5;
camera.add(cam);`),
      para("The camera follows the player with 1.5x zoom. It is a separate entity so that camera behaviour (zoom, rotation, offset) is decoupled from the player."),

      bold("Step 4 — Create an Enemy with a Detection Border:"),
      ...codeBlock(
`Entity enemy = new Entity(0, "enemy");
enemy.add(new Position());
enemy.add(new RigidBody());
enemy.add(new Sprite().setImageLink("blue8bitsqr.png"));
enemy.add(new FaceEntity(player));  // always faces the player
enemy.add(new Collision());

// Invisible circle collider that follows the enemy
Entity enemyBorder = new Entity(0, "enemyBorder");
enemyBorder.add(new ParentEntity().setParentEntity(enemy));
enemyBorder.add(new Position());
enemyBorder.add(new ScriptComponent((self, em, dt) -> {
    // Sync position to parent + react to player proximity
}));`),
      para("This demonstrates the script system: the enemyBorder has no dedicated system — its behaviour is defined inline as a lambda."),

      bold("Step 5 — Create a Coin Pickup:"),
      ...codeBlock(
`Entity coin = new Entity(0, "coin");
coin.add(new Position());
coin.add(new Sprite().setImageLink("blue8bitsqr.png"));
Collision coinCol = new Collision();
coinCol.solid = false;  // no physics push-apart
coinCol.radius = 4;
coin.add(coinCol);
coin.add(new Pickup());  // makes it collectible`),
      para("The coin has solid = false on its Collision, so it detects overlap but doesn't push the player away. When the player walks over it, the PickupSystem removes it."),

      bold("Step 6 — Create the Tile Map with Walls:"),
      ...codeBlock(
`Entity mapEntity = new Entity(0, "tilemap");
mapEntity.add(new Position());
mapEntity.get(Position.class).x -= 32; // offset the entire map
mapEntity.add(new TileMap(8)
    .setTileset("MYtileset.png")
    .setMap("test.map"));

// Spawn wall entities where tile == 1
// The origin offset must match the mapEntity's Position!
TileMapUtils.spawnEntities(
    mapEntity.get(TileMap.class), scene.entityManager,
    -32, 0, (tileIndex, wx, wy) -> {
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
    });`),
      para("The TileMap component is created with just path strings — no I/O happens here. setTileset() and setMap() store the paths; actual loading is deferred to TileMapUtils or to the TileMapSystem's lazy-load on first render."),
      para("TileMapUtils.spawnEntities() is a static utility that iterates the map grid and calls the TileEntitySpawner callback for each tile. It automatically loads the map data if it hasn't been loaded yet. The origin parameters (here -32, 0) define where tile (0,0) maps to in world space — this must match the mapEntity's Position so that the spawned wall entities align with the rendered tiles."),
      para("The map file (test.map) is a grid of integers. Tile index 0 is an empty/walkable tile, and tile index 1 is a wall. The callback creates a wall Entity for each wall tile, with an immovable RigidBody so the player cannot push through. Returning null for non-wall tiles means no entity is created for those tiles."),

      bold("Step 7 — Enable Lighting:"),
      ...codeBlock(
`scene.getLightingSystem().setEnabled(true);
scene.getLightingSystem().setAmbientDarkness(200);`),
      para("This enables the darkness overlay at alpha 200 (out of 255). The player's Light component will cut through the darkness, creating a torch effect."),

      bold("Step 8 — Add Everything to the Scene:"),
      ...codeBlock(
`scene.addEntity(mapEntity);
scene.addEntity(player);
scene.addEntity(enemy);
scene.addEntity(enemyBorder);
scene.addEntity(coin);
scene.addEntity(camera);
return scene;`),
      para("Once all entities are added, the engine starts its loop and the game is running. The explicit system ordering in Scene.update() ensures everything processes correctly each frame."),

      // ═══════════════════════════════════════════════════════════════
      // 11. UTILITIES
      // ═══════════════════════════════════════════════════════════════
      pageBreak(),
      h1("11. Utilities"),
      para("The utils package contains static helper classes that perform operations which don't belong in components (pure data) or systems (per-frame logic). These are typically one-time setup operations called during scene construction."),

      h2("11.1 TileMapUtils"),
      para("File: utils/TileMapUtils.java"),
      para("TileMapUtils provides static methods for loading tilemap data and spawning entities from map grids. These operations were extracted from the TileMap component to maintain the ECS principle that components must be plain data containers with no logic."),

      bold("Loading Methods:"),
      ...codeBlock(
`// Load just the map file — parses the .map text file into the
// TileMap's map[][] array, mapWidth, and mapHeight fields.
TileMapUtils.loadMap(TileMap tm);

// Load just the tileset — reads the tileset image, slices it
// into individual tile BufferedImages, and populates the
// tileCache[] array for O(1) lookup by tile index.
TileMapUtils.loadTileset(TileMap tm);

// Load both tileset and map, then set loaded = true.
// This is what TileMapSystem calls on first render if
// the TileMap hasn't been loaded yet.
TileMapUtils.load(TileMap tm);`),
      para("Each load method is idempotent — it checks whether the data has already been loaded before doing any work. For example, loadMap() only runs if tm.map is null and tm.mapPath is not null. This means it is safe to call these methods multiple times or from multiple places."),
      para("The loading process for the tileset works as follows: it reads the image from the sprites/ resource directory, calculates how many tiles fit per row and column based on the image dimensions and tileSize, then creates a BufferedImage for each tile by drawing the corresponding sub-rectangle of the tileset onto a new image. These are stored in tileCache[], indexed by tile ID (row * columns + col), so rendering can look up any tile in constant time."),
      para("The map loading reads a text file from the maps/ resource directory. Each line is split by whitespace into integers, producing one row of the 2D map[][] array. The dimensions (mapWidth, mapHeight) are derived from the parsed data."),

      bold("Entity Spawning:"),
      ...codeBlock(
`TileMapUtils.spawnEntities(
    TileMap tm,           // the tilemap component
    EntityManager em,     // where to register spawned entities
    double originX,       // world X of tile (0,0)
    double originY,       // world Y of tile (0,0)
    TileEntitySpawner spawner  // callback per tile
);`),
      para("spawnEntities() iterates every cell in the map grid. For each cell, it calculates the world position as (originX + col * tileSize, originY + row * tileSize) and calls the TileEntitySpawner callback with the tile index and world coordinates. If the callback returns a non-null Entity, it is added to the EntityManager."),
      para("If the map data has not been loaded yet (tm.map is null), spawnEntities() automatically calls loadMap() first. This means you can call spawnEntities() directly after creating a TileMap with just path strings — the map file will be loaded on demand."),

      bold("Important — Origin Alignment:"),
      para("The originX/originY passed to spawnEntities() must match the Position of the tilemap entity that the TileMapSystem will render. The TileMapSystem draws tiles relative to the entity's Position component, while spawnEntities() places wall/collision entities at originX/originY offsets. If these values differ, the visual tiles and the collision entities will be misaligned — players will see walls in one place but collide with invisible barriers elsewhere."),
      ...codeBlock(
`// CORRECT — both use the same offset
mapEntity.get(Position.class).x = -32;
TileMapUtils.spawnEntities(tm, em, -32, 0, spawner);

// WRONG — visual tiles at x=0, walls at x=-32
mapEntity.get(Position.class).x = 0;
TileMapUtils.spawnEntities(tm, em, -32, 0, spawner);`),

      bold("Why This Is a Utility, Not a System:"),
      para("Entity spawning from map data is a one-time scene setup operation, not something that runs every frame. It doesn't fit the System pattern (which processes entities each tick). Making it a static utility keeps the code discoverable, testable, and callable from any scene setup method without needing a system instance."),

      // Final note
      pageBreak(),
      h1("End of Document"),
      para("This document covers the complete architecture of the Simpl engine as of its current state. The engine provides a solid ECS foundation with rendering, lighting, physics, input, audio, tile mapping, scripting, and a Creator factory for fast iteration. Planned areas for future development include the animation system, UI system, and AI/pathfinding."),
    ],
  }],
});

// Generate
const buffer = await Packer.toBuffer(doc);
const outPath = "C:/Users/steph/eclipse-workspace/Simpl/Simpl_Engine_Documentation.docx";
fs.writeFileSync(outPath, buffer);
console.log("Document written to:", outPath);
