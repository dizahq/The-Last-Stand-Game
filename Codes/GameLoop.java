package Codes;

import javax.swing.SwingUtilities;
import javax.swing.JPanel;

public class GameLoop extends JPanel implements Runnable {
    private final Game game;
    private final long optimalTime;
    
    private Thread gameThread;
    private volatile boolean running = false;
    private volatile boolean paused = false;

    public GameLoop(Game game, int fps) {
        this.game = game;
        this.optimalTime = 1_000_000_000L / fps;
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        gameThread = new Thread(this, "GameLoopThread");
        SwingUtilities.invokeLater(() -> game.requestFocusInWindow);
        gameThread.start();
    }

  // Permanently stops game loop
    public void stopGameThread() {
        running = false;
        paused = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }
        System.out.println("[Game] Game stopped.");
    }

   // Resumes loop after pause
    public void resumeGameThread() {
        paused = false;
        requestFocusInWindow();
        synchronized (this) {
            notifyAll();
        }
        System.out.println("[Game] Game resumes.");
    }


    public void setPaused() {
        this.paused = paused;
        if (!paused) {
            synchronized (this) {
                this.notifyAll();
            }
        }
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
            while (lag >= optimalTime) {
                game.update();
                lag -= optimalTime;
            }

            // Render
            game.repaint();

            // Calculate sleep to save CPU
            long syncTime = (optimalTime - (System.nanoTime() - now)) / 1_000_000L;
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