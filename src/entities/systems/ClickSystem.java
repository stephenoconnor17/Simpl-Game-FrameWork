package entities.systems;

import entities.Entity;
import entities.EntityManager;
import entities.components.input.Clickable;
import entities.components.rendering.Camera;
import entities.components.rendering.Sprite;
import entities.components.rendering.UIElement;
import entities.components.transform.ParentEntity;
import entities.components.transform.Position;
import input.InputManager;

/**
 * Detects mouse clicks on entities that have a {@link Clickable} component.
 * <p>
 * Screen-space UI entities are hit-tested against virtual canvas mouse
 * coordinates directly. World entities are hit-tested after converting
 * the mouse position to world coordinates (respecting camera offset,
 * zoom, and rotation).
 * <p>
 * Runs before ScriptSystem so scripts can read {@code clickable.clicked}
 * on the same frame the click happened.
 */
public class ClickSystem implements GameSystem {

	private final InputManager im;

	public ClickSystem(InputManager im) {
		this.im = im;
	}

	@Override
	public void update(EntityManager entities, double dt) {
		// clear all clicked flags first
		for (Entity e : entities.getEntities()) {
			if (e.has(Clickable.class)) {
				e.get(Clickable.class).clicked = false;
			}
		}

		if (!im.getMouse().clicked) return;

		// consume the click
		im.getMouse().clicked = false;

		// virtual canvas mouse coordinates
		double mouseScreenX = im.getMouse().x;
		double mouseScreenY = im.getMouse().y;

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

		for (Entity e : entities.getEntities()) {
			if (!e.has(Clickable.class) || !e.has(Position.class) || !e.has(Sprite.class)) continue;

			Clickable clickable = e.get(Clickable.class);
			if (!clickable.enabled) continue;

			Sprite spr = e.get(Sprite.class);
			if (spr.image == null) continue;

			Position pos = e.get(Position.class);
			double w = spr.image.getWidth();
			double h = spr.image.getHeight();

			if (isScreenSpace(e)) {
				// screen-space UI: compare directly against virtual canvas coords
				if (mouseScreenX >= pos.x && mouseScreenX <= pos.x + w &&
					mouseScreenY >= pos.y && mouseScreenY <= pos.y + h) {
					clickable.clicked = true;
					clickable.clickX = mouseScreenX;
					clickable.clickY = mouseScreenY;
				}
			} else {
				// world entity: use world-converted mouse position
				if (worldMouseX >= pos.x && worldMouseX <= pos.x + w &&
					worldMouseY >= pos.y && worldMouseY <= pos.y + h) {
					clickable.clicked = true;
					clickable.clickX = worldMouseX;
					clickable.clickY = worldMouseY;
				}
			}
		}
	}

	private boolean isScreenSpace(Entity e) {
		if (e.has(UIElement.class)) {
			return e.get(UIElement.class).screenSpace;
		}
		Entity current = e;
		while (current.has(ParentEntity.class)) {
			Entity parent = current.get(ParentEntity.class).parentEntity;
			if (parent == null) break;
			if (parent.has(UIElement.class)) {
				return parent.get(UIElement.class).screenSpace;
			}
			current = parent;
		}
		return false;
	}
}
