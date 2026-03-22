package entities.components.physics;

import entities.components.Component;

/**
 * Marks an entity as a collectible item.
 */
public class Pickup extends Component {

	/** Identifier for the pickup kind. */
	public String type = "coin";
	/** Set to true by PickupSystem once collected. */
	public boolean collected = false;
}
