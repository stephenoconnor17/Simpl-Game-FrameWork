package core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import input.InputManager;

public class Engine implements Runnable {
	
    private GamePanel renderSurface;

    private Scene currentScene;

    private InputManager inputManager;

    private PixelStyle pixelStyle;
    private BufferedImage virtualCanvas;

    private static boolean online = false;
    
    private long tick = 0;
    private double gameTime = 0;

    private volatile boolean running = false;
    private Thread thread;

    public Engine(GamePanel gamePanel, PixelStyle pixelStyle) {
    	this.renderSurface = gamePanel;
    	this.pixelStyle = pixelStyle;
    	this.virtualCanvas = new BufferedImage(
    			pixelStyle.virtualWidth, pixelStyle.virtualHeight,
    			BufferedImage.TYPE_INT_ARGB);

    	inputManager = new InputManager();
    	inputManager.setComponent(renderSurface);
    	inputManager.getMouse().setScale(
    			(double) renderSurface.getWidth() / pixelStyle.virtualWidth,
    			(double) renderSurface.getHeight() / pixelStyle.virtualHeight);

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

   /* @Override
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
            
            if(inputManager.getKeyboard().T_key_pressed) {
            	this.stop();
            }
            
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
    */
    
    @Override
    public void run() {

        final double FIXED_STEP = 1.0 / 60.0;
        final long FRAME_NS = 1_000_000_000L / 60;  // render cap, separate concern

        long lastTime = System.nanoTime();
        double accumulator = 0;

        while (running) {

            long startTime = System.nanoTime();
            long elapsedNs = startTime - lastTime;
            lastTime = startTime;

            double frameTime = elapsedNs / 1_000_000_000.0;
            if (frameTime > 0.25) frameTime = 0.25;

            if (inputManager.getKeyboard().T_key_pressed) {
                this.stop();
            }

            accumulator += frameTime;
            while (accumulator >= FIXED_STEP) {
                update(FIXED_STEP);
                tick++;
                gameTime += FIXED_STEP;
                accumulator -= FIXED_STEP;
            }
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

    	// draw the game onto the virtual canvas
    	Graphics2D vg = virtualCanvas.createGraphics();
    	try {
    		vg.setColor(Color.BLACK);
    		vg.fillRect(0, 0, pixelStyle.virtualWidth, pixelStyle.virtualHeight);
    		currentScene.render(vg, pixelStyle.virtualWidth, pixelStyle.virtualHeight);
    	} finally {
    		vg.dispose();
    	}

    	// blit the virtual canvas to the real screen, scaled up with nearest-neighbor
    	do {
    		do {
    			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
    			try {
    				g.setRenderingHint(
    						RenderingHints.KEY_INTERPOLATION,
    						RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    				g.drawImage(virtualCanvas, 0, 0,
    						renderSurface.getWidth(), renderSurface.getHeight(), null);
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

    public static boolean isOnline() {
    	return online;
    }

    public static void setOnline(boolean online) {
    	Engine.online = online;
    }
    
    public long getTick() {
    	return this.tick;
    }
    
    public double getGameTime() {
    	return this.getGameTime();
    }
}
