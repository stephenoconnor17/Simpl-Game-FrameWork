package entities.components.transform;

import entities.Entity;
import entities.components.Component;

public class ChildEntity extends Component {
	public Entity ChildEntity;
	
	public ChildEntity setChildEntity(Entity e) {
		this.ChildEntity = e;
		return this;
	}
}
