package entities.components.physics;

import entities.components.Component;

/**
 * Marks an entity as a physics body for collision resolution.
 */
public class RigidBody extends Component {
	/** If true, entity is pushed during collision; if false, acts as a static obstacle. */
	public boolean movable = true;

	public RigidBody setMovable(boolean movable) {
		this.movable = movable;
		return this;
	}
}
