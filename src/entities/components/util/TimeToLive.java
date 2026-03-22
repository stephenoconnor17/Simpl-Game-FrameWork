package entities.components.util;

import entities.components.Component;

/**
 * Automatically destroys the entity after a duration.
 */
public class TimeToLive extends Component{
	/** Time to live in seconds. */
	public double ttl = 4;
	
	public TimeToLive setTTL(double ttl) {
		this.ttl = ttl;
		return this;
	}
}
