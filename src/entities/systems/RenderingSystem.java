package entities.systems;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import entities.Entity;
import entities.EntityManager;
import entities.components.rendering.Camera;
import entities.components.rendering.Layer;
import entities.components.rendering.Sprite;
import entities.components.rendering.Text;
import entities.components.rendering.UIElement;
import entities.components.transform.ChildOf;
import entities.components.transform.Position;

/**
 * Renders all visible entities. Split into two passes:
 * <ul>
 *   <li>{@link #renderWorld} — world entities drawn with camera transform (zoom, rotation, offset)</li>
 *   <li>{@link #renderUI} — screen-space UI entities drawn without camera transform, positioned by anchor</li>
 * </ul>
 * Scene calls renderWorld, then LightingSystem, then renderUI — so UI always draws on top of lighting.
 * Entities can have a {@link Sprite}, {@link Text}, or both.
 */
public class RenderingSystem {

	// camera state, recomputed each frame in renderWorld and reused by renderUI
	private double camX, camY, camRotation, camZoom;
	private List<Entity> worldEntities = new ArrayList<>();
	private List<Entity> screenUIEntities = new ArrayList<>();

	/**
	 * Renders world-space entities with the camera transform applied.
	 * Sorts by layer, resolves camera, then draws sprites and text.
	 */
	public void renderWorld(EntityManager entities, Graphics2D g, int screenW, int screenH) {
		List<Entity> sorted = entities.getEntities();
		sorted.sort(Comparator.comparingInt(e -> e.has(Layer.class) ? e.get(Layer.class).layerLevel : 0));

		resolveCamera(sorted, screenW, screenH);
		partition(sorted);

		// apply camera transform: zoom, rotation, and offset
		AffineTransform baseTransform = g.getTransform();
		g.translate(screenW / 2.0, screenH / 2.0);
		g.scale(camZoom, camZoom);
		g.rotate(-camRotation);
		g.translate(-screenW / 2.0, -screenH / 2.0);
		// snap the camera offset to whole virtual pixels at render time only —
		// fractional translates rasterize per-axis and cause diagonal stutter
		g.translate(-Math.round(camX), -Math.round(camY));

		for (Entity e : worldEntities) {
			Position pos = e.get(Position.class);
			AffineTransform old = g.getTransform();

			// draw sprite rotated around its center
			if (e.has(Sprite.class)) {
				Sprite spr = e.get(Sprite.class);
				if (spr.image != null) {
				// snap draw position to whole virtual pixels; logical position stays a double
				double drawX = Math.round(pos.x);
				double drawY = Math.round(pos.y);
				double centerX = drawX + spr.image.getWidth() / 2.0;
				double centerY = drawY + spr.image.getHeight() / 2.0;
				g.rotate(pos.rotation, centerX, centerY);
				g.translate(drawX, drawY);
				g.drawImage(spr.image, 0, 0, null);
				g.setTransform(old);
				old = g.getTransform();
				}
			}

			// draw text at entity position with anti-aliasing off for pixel-art
			if (e.has(Text.class)) {
				Text t = e.get(Text.class);
				g.setFont(t.font);
				g.setColor(t.colour);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				                   RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				int baseline = g.getFontMetrics().getAscent();
				g.drawString(t.text, (int) pos.x, (int) pos.y + baseline);
			}

			g.setTransform(old);
		}

		g.setTransform(baseTransform);
	}

	/**
	 * Renders screen-space UI entities without camera transform.
	 * Position is computed from UIElement anchor percentages (anchorX * screenW, anchorY * screenH).
	 */
	public void renderUI(EntityManager entities, Graphics2D g, int screenW, int screenH) {
		for (Entity e : screenUIEntities) {
			// compute screen position from anchor percentages
			UIElement ui = e.has(UIElement.class) ? e.get(UIElement.class) : null;
			double drawX = ui != null ? ui.anchorX * screenW : 0;
			double drawY = ui != null ? ui.anchorY * screenH : 0;

			AffineTransform old = g.getTransform();

			if (e.has(Sprite.class)) {
				Sprite spr = e.get(Sprite.class);
				g.translate(drawX, drawY);
				g.drawImage(spr.image, 0, 0, null);
				g.setTransform(old);
				old = g.getTransform();
			}

			if (e.has(Text.class)) {
				Text t = e.get(Text.class);
				g.setFont(t.font);
				g.setColor(t.colour);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				                   RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				int baseline = g.getFontMetrics().getAscent();
				g.drawString(t.text, (int) drawX, (int) drawY + baseline);
			}

			g.setTransform(old);
		}
	}

	/** Finds the camera entity and computes world offset, rotation, and zoom. */
	private void resolveCamera(List<Entity> sorted, int screenW, int screenH) {
		camX = 0; camY = 0; camRotation = 0; camZoom = 1.0;

		for (Entity e : sorted) {
			if (e.has(Camera.class)) {
				Camera cam = e.get(Camera.class);
				camRotation = cam.rotation;
				camZoom = cam.zoom;

				if (cam.target != null && cam.target.has(Position.class)) {
					Entity target = cam.target;
					Position tp = target.get(Position.class);
					double targetCenterX = tp.x;
					double targetCenterY = tp.y;
					if (target.has(Sprite.class) && target.get(Sprite.class).image != null) {
						targetCenterX += target.get(Sprite.class).image.getWidth() / 2.0;
						targetCenterY += target.get(Sprite.class).image.getHeight() / 2.0;
					}
					double sin = Math.sin(cam.rotation);
					double cos = Math.cos(cam.rotation);
					double rotOffX = cam.userOffsetX * cos - cam.userOffsetY * sin;
					double rotOffY = cam.userOffsetX * sin + cam.userOffsetY * cos;
					camX = targetCenterX - screenW / 2.0 + rotOffX;
					camY = targetCenterY - screenH / 2.0 + rotOffY;

					cam.offsetX = camX;
					cam.offsetY = camY;
				}
				cam.markDirty();
				break;
			}
		}
	}

	/** Splits entities into world and screen-space UI lists. Requires Sprite or Text to be renderable. */
	private void partition(List<Entity> sorted) {
		worldEntities.clear();
		screenUIEntities.clear();

		for (Entity e : sorted) {
			if (!e.has(Sprite.class) && !e.has(Text.class)) continue;

			if (isScreenSpace(e)) {
				screenUIEntities.add(e);
			} else if (e.has(Position.class)) {
				worldEntities.add(e);
			}
		}
	}

	/** Checks the entity's own UIElement, then walks up the parent chain to inherit screenSpace. */
	private boolean isScreenSpace(Entity e) {
		if (e.has(UIElement.class)) {
			return e.get(UIElement.class).screenSpace;
		}
		// walk up parent chain to inherit screenSpace from the root UI entity
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