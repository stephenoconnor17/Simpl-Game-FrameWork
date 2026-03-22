package entities.components.transform;

import entities.Entity;
import entities.components.Component;

/**
 * Links this entity to a child entity, forming a parent-child hierarchy.
 */
public class ChildEntity extends Component {
	/** The child entity attached to this one. */
	public Entity ChildEntity;
	
	public ChildEntity setChildEntity(Entity e) {
		this.ChildEntity = e;
		return this;
	}
}
