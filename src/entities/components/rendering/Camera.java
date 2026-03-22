package entities.components.rendering;

import entities.Entity;
import entities.components.Component;

/**
 * Marks an entity as the active camera. Attach to a dedicated camera entity.
 */
public class Camera extends Component{
	/** The entity the camera follows. */
	public Entity target;

	/** Manual horizontal offset from the target. */
	public double userOffsetX;
	/** Manual vertical offset from the target. */
	public double userOffsetY;

	/** Computed world offset, set by RenderingSystem each frame. */
	public double offsetX;
	/** Computed world offset, set by RenderingSystem each frame. */
	public double offsetY;
	/** Current camera rotation in radians. */
	public double rotation;
	/** Zoom level — 1.0 is default, higher is closer. */
	public double zoom = 1.0;

	/** Virtual screen width, set during render. */
	public int screenW;
	/** Virtual screen height, set during render. */
	public int screenH;

	public Camera setTarget(Entity target) {
		this.target = target;
		return this;
	}
}
