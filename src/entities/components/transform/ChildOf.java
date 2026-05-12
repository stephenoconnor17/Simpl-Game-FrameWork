package entities.components.transform;

import entities.Entity;
import entities.components.Component;

/**
 * Links this entity to a parent entity, forming a parent-child hierarchy.
 */
public class ChildOf extends Component{
	/** The parent this entity follows. */
	public Entity parentEntity;

	/** Horizontal offset from the parent's position. */
	public double offsetX = 0.0;

	/** Vertical offset from the parent's position. */
	public double offsetY = 0.0;

	/** If true, this entity's rotation is synced to the parent's rotation. */
	public boolean inheritRotation = false;

	public ChildOf setParentEntity(Entity e) {
		this.parentEntity = e;
		return this;
	}
	
	public ChildOf setOffsetX(double offsetX) {
		this.offsetX = offsetX;
		return this;
	}
	
	public ChildOf setOffsetY(double offsetY) {
		this.offsetY = offsetY;
		return this;
	}
	
	public ChildOf setInheritRotation(boolean inheritRotation) {
		this.inheritRotation = inheritRotation;
		return this;
	}
}
