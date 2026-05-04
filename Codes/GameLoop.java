package Codes;

public class GameLoop implements Runnable {
    private final Game game;
    private Thread gameThread;
    private volatile boolean running = false;
    private volatile boolean paused = false;

    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1_000_000_000L / TARGET_FPS;

    public GameLoop(Game game) {
        this.game = game;
    }


    //Create and start the game loop
    public synchronized void startThread() {
        if (gameThread != null && gameThread.isAlive()) return;

        running = true;
        paused = false;
        gameThread = new Thread(this, "GameThread");
        gameThread.start();
        System.out.println("[Game] Game loop starts.");
    }

    //Pause the game loop
    public void pauseThread(){
        paused = true;
        System.out.println("[Game] Game paused.");
    }

   // Resumes loop after pause
    public void resumeThread() {
        paused = false;
        
        synchronized (this) {
            notifyAll();
        }
        System.out.println("[Game] Game resumes.");
    }

    public void stopThread(){
        running = false;
        paused = false;

        synchronized (this) {
            notifyAll(); 
        }

        if (gameThread != null) gameThread.interrupt();
        System.out.println("[Game] Game stopped.");
    }


    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long lag = 0L;

        while (running) {
            if (paused) {
                synchronized (this) {
                    while (paused && running) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
                lastTime = System.nanoTime(); // Reset timer after pause
            }

            long now = System.nanoTime();
            long elapsed = now - lastTime;
            lastTime = now;
            lag += elapsed;

            // Fixed update steps
            while (lag >= OPTIMAL_TIME) {
                game.update();
                lag -= OPTIMAL_TIME;
            }

            // Render
            game.repaint();

            // Calculate sleep to save CPU
            long syncTime = (OPTIMAL_TIME - (System.nanoTime() - now)) / 1_000_000L;
            if (syncTime > 0) {
                try {
                    Thread.sleep(syncTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}