package input;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class Mouse implements MouseListener, MouseMotionListener{

	public int x = 0, y = 0;
	public boolean clicked = false;

	// mouse delta for locked mode (set each frame by pollLocked)
	public int deltaX = 0, deltaY = 0;
	private boolean locked = false;
	private Robot robot;
	private Component component;

	private double scaleX = 1;
	private double scaleY = 1;

	public void setScale(double scaleX, double scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	public void setLocked(boolean locked, Component component) {
		this.locked = locked;
		this.component = component;
		if (locked) {
			try {
				robot = new Robot();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// hide cursor
			component.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
					new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
					new Point(0, 0), "blank"));
			// initial recenter
			Point screenPos = component.getLocationOnScreen();
			int cx = component.getWidth() / 2;
			int cy = component.getHeight() / 2;
			robot.mouseMove(screenPos.x + cx, screenPos.y + cy);
		} else {
			component.setCursor(Cursor.getDefaultCursor());
		}
	}

	public boolean isLocked() {
		return locked;
	}

	/**
	 * Call once per frame BEFORE reading deltaX/deltaY.
	 * Polls the actual mouse position, computes delta from window center,
	 * then recenters. No event-based tracking = no race conditions.
	 */
	public void pollLocked() {
		if (!locked || robot == null || component == null) return;

		// skip when window isn't focused
		if (!component.hasFocus()) {
			deltaX = 0;
			deltaY = 0;
			return;
		}

		Point mouseScreen = MouseInfo.getPointerInfo().getLocation();
		Point compScreen = component.getLocationOnScreen();
		int localX = mouseScreen.x - compScreen.x;
		int localY = mouseScreen.y - compScreen.y;

		int centerX = component.getWidth() / 2;
		int centerY = component.getHeight() / 2;

		deltaX = localX - centerX;
		deltaY = localY - centerY;

		// recenter
		if (deltaX != 0 || deltaY != 0) {
			robot.mouseMove(compScreen.x + centerX, compScreen.y + centerY);
		}
	}

	private void updateXandY(int newX, int newY) {
		this.x = (int) (newX / scaleX);
		this.y = (int) (newY / scaleY);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		updateXandY(e.getX(),e.getY());
		clicked = true;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		updateXandY(e.getX(),e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		updateXandY(e.getX(),e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		updateXandY(e.getX(),e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateXandY(e.getX(),e.getY());
	}

}
