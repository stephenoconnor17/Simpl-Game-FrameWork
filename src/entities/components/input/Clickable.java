package entities.components.input;

import entities.components.Component;

/**
 * Marks an entity as __clickable__. When the mouse clicks within this entity's
 * hit-box, {@code clicked} is set to {@code true} for one frame and the click
 * position is stored in {@code clickX}/{@code clickY} (world coordinates for
 * world entities, screen coordinates for screen-space UI).
 * <p>
 * The hit-box is defined by {@code width} and {@code height}. When either is
 * 0, it falls back to the entity's {@link entities.components.rendering.Sprite Sprite}
 * dimensions. If no sprite is present and no bounds are set, the entity is
 * skipped by {@link entities.systems.ClickSystem ClickSystem} and a warning
 * is logged.
 * <p>
 * {@code hovered} is {@code true} for every frame the mouse is inside the
 * hit-box. {@code pressed} is {@code true} for every frame the mouse button
 * is held down while inside the hit-box. Both are continuous states managed
 * by {@link entities.systems.ClickSystem ClickSystem}, unlike {@code clicked}
 * which is a single-frame event.
 * <p>
 * The {@link entities.systems.ClickSystem ClickSystem} clears {@code clicked},
 * {@code hovered}, and {@code pressed} each frame before re-evaluating.
 */
public class Clickable extends Component {
	/** Whether click detection is active. */
	public boolean enabled = true;

	/** True for one frame after a click lands on this entity. Managed by ClickSystem. */
	public boolean clicked = false;

	/** True for every frame the mouse is inside the hit-box. Managed by ClickSystem. */
	public boolean hovered = false;

	/** True for every frame the mouse button is held down while inside the hit-box. Managed by ClickSystem. */
	public boolean pressed = false;

	/** X position of the click. Managed by ClickSystem. */
	public double clickX = 0;

	/** Y position of the click. Managed by ClickSystem. */
	public double clickY = 0;

	/** Hit-box width. If 0, falls back to the entity's sprite width. */
	public double width = 0;

	/** Hit-box height. If 0, falls back to the entity's sprite height. */
	public double height = 0;

	public Clickable setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public Clickable setBounds(double width, double height) {
		this.width = width;
		this.height = height;
		return this;
	}
}