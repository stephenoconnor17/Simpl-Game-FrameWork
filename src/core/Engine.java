package core;

public class Engine implements Runnable {

    private static final double FPS = 60.0;	//change this to cap FPS.
    private static final double FRAME_TIME = 1_000_000_000.0 / FPS; // nanoseconds
    
    private GamePanel renderSurface;

    private volatile boolean running = false;
    private Thread thread;
    
    public Engine(GamePanel gamePanel) {
    	this.renderSurface = gamePanel;
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
            render(); // usually repaint()

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
        // scene.update(dt);
    }

    private void render() {
        // renderSurface.repaint();
    }
}
