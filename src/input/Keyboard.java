package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener{

	//standard movement keys
	public boolean W_key_pressed = false;
	public boolean S_key_pressed = false;
	public boolean A_key_pressed = false;
	public boolean D_key_pressed = false;

	//top row without w
	public boolean Q_key_pressed = false;
	public boolean E_key_pressed = false;
	public boolean R_key_pressed = false;
	public boolean T_key_pressed = false;
	public boolean Y_key_pressed = false;
	public boolean U_key_pressed = false;
	public boolean I_key_pressed = false;
	public boolean O_key_pressed = false;
	public boolean P_key_pressed = false;

	//middle row without a, s, d
	public boolean F_key_pressed = false;
	public boolean G_key_pressed = false;
	public boolean H_key_pressed = false;
	public boolean J_key_pressed = false;
	public boolean K_key_pressed = false;
	public boolean L_key_pressed = false;

	//bottom row
	public boolean Z_key_pressed = false;
	public boolean X_key_pressed = false;
	public boolean C_key_pressed = false;
	public boolean V_key_pressed = false;
	public boolean B_key_pressed = false;
	public boolean N_key_pressed = false;
	public boolean M_key_pressed = false;


	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		setKey(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		setKey(e.getKeyCode(), false);
	}

	private void setKey(int keyCode, boolean state) {
		switch (keyCode) {
			case KeyEvent.VK_W: W_key_pressed = state; break;
			case KeyEvent.VK_A: A_key_pressed = state; break;
			case KeyEvent.VK_S: S_key_pressed = state; break;
			case KeyEvent.VK_D: D_key_pressed = state; break;
			case KeyEvent.VK_Q: Q_key_pressed = state; break;
			case KeyEvent.VK_E: E_key_pressed = state; break;
			case KeyEvent.VK_R: R_key_pressed = state; break;
			case KeyEvent.VK_T: T_key_pressed = state; break;
			case KeyEvent.VK_Y: Y_key_pressed = state; break;
			case KeyEvent.VK_U: U_key_pressed = state; break;
			case KeyEvent.VK_I: I_key_pressed = state; break;
			case KeyEvent.VK_O: O_key_pressed = state; break;
			case KeyEvent.VK_P: P_key_pressed = state; break;
			case KeyEvent.VK_F: F_key_pressed = state; break;
			case KeyEvent.VK_G: G_key_pressed = state; break;
			case KeyEvent.VK_H: H_key_pressed = state; break;
			case KeyEvent.VK_J: J_key_pressed = state; break;
			case KeyEvent.VK_K: K_key_pressed = state; break;
			case KeyEvent.VK_L: L_key_pressed = state; break;
			case KeyEvent.VK_Z: Z_key_pressed = state; break;
			case KeyEvent.VK_X: X_key_pressed = state; break;
			case KeyEvent.VK_C: C_key_pressed = state; break;
			case KeyEvent.VK_V: V_key_pressed = state; break;
			case KeyEvent.VK_B: B_key_pressed = state; break;
			case KeyEvent.VK_N: N_key_pressed = state; break;
			case KeyEvent.VK_M: M_key_pressed = state; break;
		}
	}

}
