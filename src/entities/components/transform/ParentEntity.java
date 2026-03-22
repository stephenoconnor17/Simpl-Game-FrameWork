package entities.components.transform;

import entities.Entity;
import entities.components.Component;

/**
 * Links this entity to a parent entity, forming a parent-child hierarchy.
 */
public class ParentEntity extends Component{
	/** The parent this entity follows. */
	public Entity parentEntity;
	
	public ParentEntity setParentEntity(Entity e) {
		this.parentEntity = e;
		return this;
	}
}
