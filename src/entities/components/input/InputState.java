package entities.components.input;

import entities.components.Component;

public class InputState extends Component {
	public boolean movingUp = false;
	public boolean movingDown = false;
	public boolean movingLeft = false;
	public boolean movingRight = false;
	
	public int mouseX = 0;
	public int mouseY = 0;
}
