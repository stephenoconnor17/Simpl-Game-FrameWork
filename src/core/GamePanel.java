package core;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JPanel;

public class GamePanel extends Canvas{
	
	public GamePanel() {
        setIgnoreRepaint(true); // we own all painting
    }

    public void initBuffering() {
        createBufferStrategy(2);
    }

    public Graphics2D getDrawGraphics() {
        return (Graphics2D) getBufferStrategy().getDrawGraphics();
    }

    public void swapBuffers() {
        BufferStrategy bs = getBufferStrategy();
        if (!bs.contentsLost()) {
            bs.show();
        }
    }
}
