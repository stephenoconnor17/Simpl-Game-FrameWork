package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener{
	
	//standard movement keys
	public boolean W_key_pressed = false;
	public boolean S_key_pressed = false;
	public boolean A_key_pressed = false;
	public boolean D_key_pressed = false;
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_W) {
			//W pressed
			W_key_pressed = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_S) {
			//W pressed
			S_key_pressed = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_A) {
			//W pressed
			A_key_pressed = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_D) {
			//W pressed
			D_key_pressed = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_W) {
			//W released
			W_key_pressed = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_S) {
			//S released
			S_key_pressed = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_A) {
			//A released
			A_key_pressed = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_D) {
			//D released
			D_key_pressed = false;
		}
	}

}
