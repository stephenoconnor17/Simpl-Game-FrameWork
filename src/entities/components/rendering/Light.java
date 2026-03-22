package entities.components.rendering;

import java.awt.Color;

import entities.components.Component;

/**
 * Emits light from the entity's position, subtracting from ambient darkness.
 */
public class Light extends Component {
	/** Light reach in world units. */
	public double radius = 32;
	/** 0.0–1.0 — how much darkness to erase. */
	public double intensity = 1.0;
	/** Tint of the light. */
	public Color color = Color.WHITE;

	public Light setRadius(double radius) {
		this.radius = radius;
		return this;
	}

	public Light setIntensity(double intensity) {
		this.intensity = intensity;
		return this;
	}

	public Light setColor(Color color) {
		this.color = color;
		return this;
	}
}
