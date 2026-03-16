package entities.components.physics;

import entities.components.Component;

public class RigidBody extends Component {
	public boolean movable = true;

	public RigidBody setMovable(boolean movable) {
		this.movable = movable;
		return this;
	}
}
