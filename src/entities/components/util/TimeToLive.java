package entities.components.util;

import entities.components.Component;

public class TimeToLive extends Component{
	public double ttl = 4;
	
	public TimeToLive setTTL(double ttl) {
		this.ttl = ttl;
		return this;
	}
}
