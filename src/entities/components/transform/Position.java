package entities.components.transform;

import entities.components.Component;

/**
 * World-space transform for an entity.
 */
public class Position extends Component {
	/** Horizontal position in world units. */
	public double x = 0.0;
	/** Vertical position in world units. */
	public double y = 0.0;
	/** Angle in radians. */
	public double rotation = 0.0;
}
