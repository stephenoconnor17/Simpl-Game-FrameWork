package entities.components.input;

import entities.components.Component;

public class InputState extends Component {
	public boolean movingUp = false;
	public boolean movingDown = false;
	public boolean movingLeft = false;
	public boolean movingRight = false;
	
	public int mouseX = 0;
	public int mouseY = 0;
	
	public int targetX = 0;
	public int targetY = 0;
	
	public boolean isMovingToTarget = false;
	public boolean clickToMove = false;
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
