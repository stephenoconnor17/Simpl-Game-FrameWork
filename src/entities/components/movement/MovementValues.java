package entities.components.movement;

import entities.components.Component;

/**
 * Movement speed and current velocity for an entity.
 */
public class MovementValues extends Component {
	/** Movement speed in units per second. */
	public double speed = 50.0;
	/** Current horizontal velocity. */
	public double velocityX = 0.0;
	/** Current vertical velocity. */
	public double velocityY = 0.0;
	
	public MovementValues setSpeed(double newSpeed) {
		this.speed = newSpeed;
		return this;
	}
	
	public MovementValues setVelocityXY(double newVelocityX, double newVelocityY) {
		this.velocityX = newVelocityX;
		this.velocityY = newVelocityY;
		return this;
	}
	
	
}
