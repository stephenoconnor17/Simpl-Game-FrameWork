package entities.components.input;

import entities.components.Component;

/**
 * Tags an entity as controlled by player input.
 */
public class PlayerControlled extends Component {
	/** Enables/disables player control. */
	public boolean active = true;
}
