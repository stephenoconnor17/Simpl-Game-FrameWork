package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import input.InputManager;

public class Engine implements Runnable {
	
    private GamePanel renderSurface;
    
    private Scene currentScene;
    
    private InputManager inputManager;
    
    private volatile boolean running = false;
    private Thread thread;
    
    public Engine(GamePanel gamePanel) {
    	this.renderSurface = gamePanel;
    	inputManager = new InputManager();
    	
    	this.renderSurface.addKeyListener(inputManager.getKeyboard());
    	this.renderSurface.addMouseListener(inputManager.getMouse());
    	this.renderSurface.addMouseMotionListener(inputManager.getMouse());
    	this.renderSurface.setFocusable(true);
    	this.renderSurface.requestFocus();
    }
    
    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {

        final int FPS = 60;
        final long FRAME_NS = 1_000_000_000L / FPS; // nanoseconds per frame

        long lastTime = System.nanoTime();

        while (running) {

            long startTime = System.nanoTime();
            long elapsedNs = startTime - lastTime;
            lastTime = startTime;

            double dt = elapsedNs / 1_000_000_000.0; // convert once to seconds

            // Optional clamp to prevent huge jumps (e.g., debugger pause)
            if (dt > 0.25) dt = 0.25;

            update(dt);
            render();

            long workTimeNs = System.nanoTime() - startTime;
            long remainingNs = FRAME_NS - workTimeNs;

            if (remainingNs > 0) {
                try {
                    Thread.sleep(
                            remainingNs / 1_000_000L,
                            (int) (remainingNs % 1_000_000L)
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    
    private void update(double dt) {
        //scene.update(dt);
    	currentScene.update(dt);
    }

    private void render() {
    	BufferStrategy bs = renderSurface.getBufferStrategy();
    	if (bs == null) return;
    	do {
    		do {
    			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
    			try {
    				g.setColor(Color.BLACK);
    				g.fillRect(0, 0, renderSurface.getWidth(), renderSurface.getHeight());
    				currentScene.render(g);
    			} finally {
    				g.dispose();
    			}
    		} while (bs.contentsRestored());
    	} while (bs.contentsLost());
    	bs.show();
    }
    
    public void setScene(Scene scene) {
    	this.currentScene = scene;
    }
    
    public InputManager getInputManager() {
    	return this.inputManager;
    }
}
