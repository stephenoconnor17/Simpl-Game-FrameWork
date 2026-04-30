package entities.components.transform;

import entities.Entity;
import entities.components.Component;

/**
 * Links this entity to a child entity, forming a parent-child hierarchy.
 */
public class ParentOf extends Component {
	/** The child entity attached to this one. */
	public Entity childEntity;

	public ParentOf setChildEntity(Entity e) {
		this.childEntity = e;
		return this;
	}
}
