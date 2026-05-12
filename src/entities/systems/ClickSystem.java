package entities.systems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entities.Entity;
import entities.EntityManager;
import entities.components.input.Clickable;
import entities.components.rendering.Camera;
import entities.components.rendering.Layer;
import entities.components.rendering.Sprite;
import entities.components.rendering.UIElement;
import entities.components.transform.ChildOf;
import entities.components.transform.Position;
import input.InputManager;

/**
 * Detects mouse hover, press, and click on entities that have a {@link Clickable}
 * component.
 * <p>
 * Screen-space UI entities are hit-tested against virtual canvas mouse
 * coordinates, positioned by their {@link UIElement} anchor. World entities
 * are hit-tested after converting the mouse position to world coordinates
 * (respecting camera offset, zoom, and rotation).
 * <p>
 * Hit-box dimensions come from {@code Clickable.width}/{@code height} when
 * non-zero, otherwise fall back to the entity's {@link Sprite} dimensions.
 * An entity with no bounds and no sprite is skipped and a one-time warning
 * is logged.
 * <p>
 * Click consumption: screen-space clickables are processed first, sorted by
 * layer descending so the visually-on-top entity wins overlaps. If any
 * screen-space clickable is hit, the entire world pass is skipped for that
 * frame — preventing UI clicks from also firing world interactions beneath.
 * <p>
 * Runs before ScriptSystem so scripts can read {@code clickable.clicked},
 * {@code hovered}, and {@code pressed} on the same frame the state updates.
 */
public class ClickSystem implements GameSystem {

	private final InputManager im;
	/** Tracks entities already warned about missing bounds to avoid log spam. */
	private final Set<Entity> warnedNoBounds = new HashSet<>();

	public ClickSystem(InputManager im) {
		this.im = im;
	}

	@Override
	public void update(EntityManager entities, double dt) {
		// clear all per-frame state first
		for (Entity e : entities.getEntities()) {
			if (e.has(Clickable.class)) {
				Clickable c = e.get(Clickable.class);
				c.clicked = false;
				c.hovered = false;
				c.pressed = false;
			}
		}
		
		

		// virtual canvas mouse coordinates
		double mouseScreenX = im.getMouse().x;
		double mouseScreenY = im.getMouse().y;
		boolean mouseDown = im.getMouse().down;
		boolean clickEvent = im.getMouse().clicked;
		//if (clickEvent) im.getMouse().clicked = false;

		// find camera for screen-to-world conversion
		double camOffX = 0, camOffY = 0;
		double camRotation = 0;
		double camZoom = 1.0;
		int screenW = 0, screenH = 0;
		for (Entity e : entities.getEntities()) {
			if (e.has(Camera.class)) {
				Camera cam = e.get(Camera.class);
				camOffX = cam.offsetX;
				camOffY = cam.offsetY;
				camRotation = cam.rotation;
				camZoom = cam.zoom;
				screenW = cam.screenW;
				screenH = cam.screenH;
				break;
			}
		}

		// screen-to-world conversion
		double smx = mouseScreenX - screenW / 2.0;
		double smy = mouseScreenY - screenH / 2.0;
		smx /= camZoom;
		smy /= camZoom;
		double sinR = Math.sin(camRotation);
		double cosR = Math.cos(camRotation);
		double worldMouseX = smx * cosR - smy * sinR + screenW / 2.0 + camOffX;
		double worldMouseY = smx * sinR + smy * cosR + screenH / 2.0 + camOffY;

		// partition clickables into screen-space and world lists
		List<Entity> screenClickables = new ArrayList<>();
		List<Entity> worldClickables = new ArrayList<>();
		for (Entity e : entities.getEntities()) {
			if (!e.has(Clickable.class)) continue;
			if (!e.get(Clickable.class).enabled) continue;
			if (isScreenSpace(e)) screenClickables.add(e);
			else worldClickables.add(e);
		}

		// screen-space pass: sort by layer descending so on-top entities win overlaps
		screenClickables.sort(Comparator.comparingInt(
			(Entity e) -> e.has(Layer.class) ? e.get(Layer.class).layerLevel : 0
		).reversed());

		boolean clickConsumed = false;

		// pass 1: screen-space clickables
		for (Entity e : screenClickables) {
			Clickable clickable = e.get(Clickable.class);

			double[] bounds = resolveBounds(e, clickable);
			if (bounds == null) continue;
			double w = bounds[0], h = bounds[1];

			if (!e.has(UIElement.class)) continue;
			UIElement ui = e.get(UIElement.class);
			double drawX = ui.anchorX * screenW;
			double drawY = ui.anchorY * screenH;

			boolean inside = mouseScreenX >= drawX && mouseScreenX <= drawX + w
				          && mouseScreenY >= drawY && mouseScreenY <= drawY + h;

			if (inside && !clickConsumed) {
				clickable.hovered = true;
				clickable.pressed = mouseDown;
				if (clickEvent) {
					clickable.clicked = true;
					clickable.clickX = mouseScreenX;
					clickable.clickY = mouseScreenY;
					clickable.markDirty();
				}
				clickConsumed = true;
			}
			
			if (clickConsumed) {
			    im.getMouse().clicked = false;
			    return;
			}
		}
		
		

		// pass 2: world clickables — skipped entirely if a UI element consumed the input
		if (clickConsumed) return;

		for (Entity e : worldClickables) {
			Clickable clickable = e.get(Clickable.class);

			double[] bounds = resolveBounds(e, clickable);
			if (bounds == null) continue;
			double w = bounds[0], h = bounds[1];

			if (!e.has(Position.class)) continue;
			Position pos = e.get(Position.class);

			boolean inside = worldMouseX >= pos.x && worldMouseX <= pos.x + w
				          && worldMouseY >= pos.y && worldMouseY <= pos.y + h;

			if (inside) {
				clickable.hovered = true;
				clickable.pressed = mouseDown;
				if (clickEvent) {
					clickable.clicked = true;
					clickable.clickX = worldMouseX;
					clickable.clickY = worldMouseY;
					clickable.markDirty();
				}
			}
		}
	}

	/**
	 * Resolves the hit-box dimensions for an entity. Returns {@code {w, h}} or
	 * {@code null} if no bounds can be determined. Logs a one-time warning per
	 * entity that has neither explicit bounds nor a sprite.
	 */
	private double[] resolveBounds(Entity e, Clickable clickable) {
		double w = clickable.width;
		double h = clickable.height;
		if (w != 0 && h != 0) return new double[]{w, h};

		if (e.has(Sprite.class) && e.get(Sprite.class).image != null) {
			Sprite spr = e.get(Sprite.class);
			if (w == 0) w = spr.image.getWidth();
			if (h == 0) h = spr.image.getHeight();
			return new double[]{w, h};
		}

		if (warnedNoBounds.add(e)) {
			System.err.println("Clickable on entity '" + e.getEntityName()
				+ "' has no bounds (no Sprite and no explicit width/height set) — skipping.");
		}
		return null;
	}

	/** Checks the entity's own UIElement, then walks up the parent chain to inherit screenSpace. */
	private boolean isScreenSpace(Entity e) {
		if (e.has(UIElement.class)) {
			return e.get(UIElement.class).screenSpace;
		}
		Entity current = e;
		while (current.has(ChildOf.class)) {
			Entity parent = current.get(ChildOf.class).parentEntity;
			if (parent == null) break;
			if (parent.has(UIElement.class)) {
				return parent.get(UIElement.class).screenSpace;
			}
			current = parent;
		}
		return false;
	}
}