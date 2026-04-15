package entities.components.physics;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import entities.components.Component;

/**
 * Defines a collision shape and filtering for an entity.
 */
public class Collision extends Component {

	public enum Shape { BOX, CIRCLE }

	/** BOX or CIRCLE collider type. */
	public Shape shape = Shape.BOX;

	/** Box collider width. */
	public double width = 8.0;
	/** Box collider height. */
	public double height = 8.0;

	/** Circle collider radius. */
	public double radius = 24.0;

	/** Whether this collider blocks movement. */
	public boolean solid = true;

	/** Horizontal offset from entity center. */
	public double offsetX = 0.0;
	/** Vertical offset from entity center. */
	public double offsetY = 0.0;

	/** Bit flag identifying what this entity is. */
	public int layer = 1;
	/** Bit flag for what layers this entity collides with (-1 = all). */
	public int mask = -1;

	/** Entities collided with this frame, cleared each update by PhysicsSystem. */
	public List<Entity> collidedWith = new ArrayList<>();

	public Collision setShape(Shape shape) {
		this.shape = shape;
		return this;
	}

	public Collision setSize(double width, double height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public Collision setRadius(double radius) {
		this.radius = radius;
		return this;
	}

	public Collision setSolid(boolean solid) {
		this.solid = solid;
		return this;
	}

	public Collision setOffset(double offsetX, double offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		return this;
	}

	public Collision setLayer(int layer) {
		this.layer = layer;
		return this;
	}

	public Collision setMask(int mask) {
		this.mask = mask;
		return this;
	}
}
