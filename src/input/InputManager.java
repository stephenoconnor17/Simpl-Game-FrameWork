package input;

public class InputManager {
	
	Keyboard keyboard;
	Mouse mouse;
	
	public InputManager(){
		keyboard = new Keyboard();
		mouse = new Mouse();
	}
	
	public Keyboard getKeyboard() {
		return this.keyboard;
	}
	
	public Mouse getMouse() {
		return this.mouse;
	}
	
	public void setKeyboard(Keyboard k) {
		this.keyboard = k;
	}
	
	public void setMouse(Mouse m) {
		this.mouse = m;
	}
}
