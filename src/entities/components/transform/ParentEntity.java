package entities.components.transform;

import entities.Entity;
import entities.components.Component;

public class ParentEntity extends Component{
	public Entity parentEntity;
	
	public ParentEntity setParentEntity(Entity e) {
		this.parentEntity = e;
		return this;
	}
}
