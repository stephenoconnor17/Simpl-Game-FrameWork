package entities.components.rendering;

import entities.Entity;
import entities.components.Component;

/**
 * Rotates the entity's sprite to face another entity.
 * Requires a target entity passed via constructor.
 */
public class FaceEntity extends Component{
	/** The entity to look at. */
	Entity toFace;

	/** Enables/disables the behaviour. */
	public boolean faceEntity = true;
	
	public FaceEntity(Entity e) {
		this.toFace = e;
	}
	
	public void setEntityToFace(Entity e) {
		this.toFace = e;
	}
	
	public Entity getEntityToFace() {
		return this.toFace;
	}
}
