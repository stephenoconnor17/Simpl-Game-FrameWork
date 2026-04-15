package entities.components.rendering;

import entities.components.Component;

/**
 * Rotates the camera view based on mouse movement (FPS-style look).
 * Requires mouse locked mode.
 */
public class RotateViewToMouse extends Component {
	/** Enables/disables rotation. */
	public boolean enabled = true;
	/** Current accumulated rotation angle. */
	public double angle = 0;
	/** Mouse-to-rotation multiplier. */
	public double sensitivity = 0.003;

	public RotateViewToMouse setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public RotateViewToMouse setSensitivity(double sensitivity) {
		this.sensitivity = sensitivity;
		return this;
	}
}
