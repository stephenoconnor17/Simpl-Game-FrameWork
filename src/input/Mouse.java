package input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener{
	
	public int x = 0, y = 0;
	
	
	public void updateXandY(int newX, int newY) {
		this.x = newX;
		this.y = newY;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		updateXandY(e.getX(),e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		updateXandY(e.getX(),e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		//MOUSE MOVE WHILE CLICKED
		
		updateXandY(e.getX(),e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//MOUSE MOVED
		
		updateXandY(e.getX(),e.getY());
	}

}
