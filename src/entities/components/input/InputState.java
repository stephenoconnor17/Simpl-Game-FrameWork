package entities.components.input;

import entities.components.Component;

/**
 * Stores the current input state for a player-controlled entity.
 * Updated by InputManager each frame.
 */
public class InputState extends Component {
	/** True while the up key is held. */
	public boolean movingUp = false;
	/** True while the down key is held. */
	public boolean movingDown = false;
	/** True while the left key is held. */
	public boolean movingLeft = false;
	/** True while the right key is held. */
	public boolean movingRight = false;

	/** Current mouse X position. */
	public int mouseX = 0;
	/** Current mouse Y position. */
	public int mouseY = 0;

	/** Click-to-move destination X. */
	public int targetX = 0;
	/** Click-to-move destination Y. */
	public int targetY = 0;
	
	/** True while navigating to a click target. */
	public boolean isMovingToTarget = false;
	/** Enables click-to-move input mode. */
	public boolean clickToMove = false;
	/** Enables keyboard directional input mode. */
	public boolean keyboardToMove = false;
	
	public InputState setClickToMove(boolean set) {
		this.clickToMove = set;
		return this;
	}
	
	public InputState setKeyboardToMove(boolean set) {
		this.keyboardToMove = set;
		return this;
	}
}
