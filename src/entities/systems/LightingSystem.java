package entities.systems;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import entities.Entity;
import entities.EntityManager;
import entities.components.rendering.Camera;
import entities.components.rendering.Light;
import entities.components.rendering.Sprite;
import entities.components.transform.Position;

public class LightingSystem {

	private boolean enabled = true;
	private int ambientDarkness = 180; // 0 (no darkness) to 255 (pitch black)

	public void render(EntityManager entities, Graphics2D g, int screenW, int screenH) {
		if (!enabled || ambientDarkness <= 0) return;

		// create the darkness overlay at virtual canvas size
		BufferedImage lightMap = new BufferedImage(screenW, screenH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D lg = lightMap.createGraphics();

		// fill with ambient darkness
		lg.setColor(new Color(0, 0, 0, ambientDarkness));
		lg.fillRect(0, 0, screenW, screenH);

		// find camera transform values (mirrors RenderingSystem logic)
		double camX = 0, camY = 0, camZoom = 1.0, camRotation = 0;
		for (Entity e : entities.getEntities()) {
			if (e.has(Camera.class)) {
				Camera cam = e.get(Camera.class);
				camX = cam.offsetX;
				camY = cam.offsetY;
				camZoom = cam.zoom;
				camRotation = cam.rotation;
				break;
			}
		}

		// apply the same camera transform as RenderingSystem
		AffineTransform camTransform = new AffineTransform();
		camTransform.translate(screenW / 2.0, screenH / 2.0);
		camTransform.scale(camZoom, camZoom);
		camTransform.rotate(-camRotation);
		camTransform.translate(-screenW / 2.0, -screenH / 2.0);
		camTransform.translate(-camX, -camY);

		// erase darkness where lights are
		lg.setComposite(AlphaComposite.DstOut);

		for (Entity e : entities.getEntities()) {
			if (!e.has(Light.class) || !e.has(Position.class)) continue;

			Light light = e.get(Light.class);
			Position pos = e.get(Position.class);

			// center the light on the entity's sprite if it has one
			double cx = pos.x;
			double cy = pos.y;
			if (e.has(Sprite.class) && e.get(Sprite.class).image != null) {
				cx += e.get(Sprite.class).image.getWidth() / 2.0;
				cy += e.get(Sprite.class).image.getHeight() / 2.0;
			}

			// transform world position to screen position
			Point2D worldPt = new Point2D.Double(cx, cy);
			Point2D screenPt = camTransform.transform(worldPt, null);
			double sx = screenPt.getX();
			double sy = screenPt.getY();
			double scaledRadius = light.radius * camZoom;

			// alpha based on intensity
			int alpha = (int) (255 * Math.max(0, Math.min(1, light.intensity)));
			Color centerColor = new Color(
				light.color.getRed(), light.color.getGreen(), light.color.getBlue(), alpha
			);
			Color edgeColor = new Color(
				light.color.getRed(), light.color.getGreen(), light.color.getBlue(), 0
			);

			RadialGradientPaint glow = new RadialGradientPaint(
				new Point2D.Double(sx, sy),
				(float) Math.max(1, scaledRadius),
				new float[]{0f, 1f},
				new Color[]{centerColor, edgeColor}
			);

			lg.setPaint(glow);
			lg.fillOval(
				(int) (sx - scaledRadius),
				(int) (sy - scaledRadius),
				(int) (scaledRadius * 2),
				(int) (scaledRadius * 2)
			);
		}

		lg.dispose();

		// draw the light map over the scene
		AffineTransform old = g.getTransform();
		g.setTransform(new AffineTransform()); // identity — lightmap is already in screen space
		g.drawImage(lightMap, 0, 0, null);
		g.setTransform(old);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public LightingSystem setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public int getAmbientDarkness() {
		return ambientDarkness;
	}

	public LightingSystem setAmbientDarkness(int ambientDarkness) {
		this.ambientDarkness = Math.max(0, Math.min(255, ambientDarkness));
		return this;
	}
}
