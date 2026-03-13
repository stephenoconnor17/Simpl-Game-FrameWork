package entities.components.rendering;

import entities.Entity;
import entities.components.Component;

public class FaceEntity extends Component{
	Entity toFace;
	
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
