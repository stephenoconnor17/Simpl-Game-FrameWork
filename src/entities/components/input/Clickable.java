package entities.components.input;

import entities.components.Component;

/**
 * Marks an entity as clickable. When the mouse clicks within this entity's
 * sprite bounds, {@code clicked} is set to {@code true} for one frame and
 * the click position is stored in {@code clickX}/{@code clickY} (world
 * coordinates for world entities, screen coordinates for screen-space UI).
 * <p>
 * The {@link entities.systems.ClickSystem ClickSystem} clears {@code clicked}
 * each frame before re-evaluating.
 */
public class Clickable extends Component {
	/** Whether click detection is active. */
	public boolean enabled = true;
	/** True for one frame after a click lands on this entity. Managed by ClickSystem. */
	public boolean clicked = false;
	/** X position of the click. Managed by ClickSystem. */
	public double clickX = 0;
	/** Y position of the click. Managed by ClickSystem. */
	public double clickY = 0;

	public Clickable setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
}
