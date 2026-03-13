package entities.components.physics;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import entities.components.Component;

public class Collision extends Component {

	public enum Shape { BOX, CIRCLE }

	public Shape shape = Shape.BOX;

	// box dimensions
	public double width = 48.0;
	public double height = 48.0;

	// circle radius
	public double radius = 24.0;

	public boolean solid = true;

	// offset from auto-centered position
	public double offsetX = 0.0;
	public double offsetY = 0.0;

	// collision filtering
	public int layer = 1;  // what I am (bit flag)
	public int mask = -1;  // what I collide with (-1 = all layers)

	// entities collided with this frame (cleared by PhysicsSystem)
	public List<Entity> collidedWith = new ArrayList<>();
}
