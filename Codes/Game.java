package Codes;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Game extends JPanel implements Runnable {
    private Thread gameThread;
    private volatile boolean running = false;
    private volatile boolean paused = false;

    // 60 frames per sec
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1_000_000_000L / TARGET_FPS;

    private Player player;
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private static final int ENEMY_COUNT = 3; // how many enemies you want
    private Image grassImage;

    private int currentLevel;
    private int lives = 4;

    private final Set<Integer> heldKeys = java.util.Collections.synchronizedSet(new HashSet<>());
    
    private GameLayeredPane gameContainer;
    private JButton pauseBtn = new JButton("Pause");

    public Game(GameLayeredPane gameContainer) {
        this.gameContainer = gameContainer;

        // Panel setup
        setBounds(0, 0, TheLastStand.getFrameWidth(), TheLastStand.getFrameHeight());
        setLayout(null);
        setFocusable(true);

        // Load assets
        grassImage = new ImageIcon("Entities/Background/grass.png").getImage();
        
        // Initialize game objects
        obstacles.add(new Obstacle(200, 200, 100, 100));
        obstacles.add(new Obstacle(400, 300, 100, 100));
        obstacles.add(new Obstacle(600, 150, 100, 100));
        player = new Player(300, 100, obstacles, this);

        for (int i = 0; i < ENEMY_COUNT; i++) {
            Enemy e = new Enemy(0, 0);
            e.respawn(); // random edge position
            enemies.add(e);
        }

        // Pause button
        pauseBtn.setBounds(TheLastStand.getFrameWidth() - 125, 20, 100, 40);
        pauseBtn.setFocusable(false);
        pauseBtn.addActionListener(e -> {
            pauseGameThread(); // stop loop
            gameContainer.getPauseMenu().setVisible(true); // show pause UI
        });
        add(pauseBtn);

        // Keylisteners
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                heldKeys.add(e.getKeyCode());
            }
            @Override
            public void keyReleased(KeyEvent e) {
                heldKeys.remove(e.getKeyCode());
            }
        });

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                requestFocusInWindow();
            }
        });
    }

    // Creates & starts game thread
    public void startGameThread() {
        if (gameThread != null && gameThread.isAlive()) return;

        running = true;
        paused = false;
        gameThread = new Thread(this, "GameThread");
        gameThread.setDaemon(true);
        gameThread.start();

        // test
        System.out.println("[Game] Game loop starts.");
    }

    // Pause loop
    public void pauseGameThread() {
        paused = true;

        // test
        System.out.println("[Game] Game paused.");
    }

    // Resumes loop after pause
    public void resumeGameThread() {
        paused = false;
        requestFocusInWindow();
        synchronized (this) {
            notifyAll(); // wake thread from wait
        }

        // test
        System.out.println("[Game] Game resumes.");
    }

    // Permanently stops game loop
    public void stopGameThread() {
        running = false;
        paused = false;
        if (gameThread != null) {
            gameThread.interrupt(); 
        }

        // test
        System.out.println("[Game] Game stopped.");
    }

    // Game loop
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long lag = 0L;

        while (running) { 
            // Pause handling
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
                lastTime = System.nanoTime();
                lag = 0L;
            }

            long now = System.nanoTime();
            long elapsed = now - lastTime;
            lastTime = now;
            lag += elapsed;

            while (lag >= OPTIMAL_TIME) {
                update();
                lag -= OPTIMAL_TIME;
            }

            // Render
            repaint();

            // Sleep for remaining time frame
            long sleepMillis = (OPTIMAL_TIME - (System.nanoTime() - now)) / 1_000_000L;
            if (sleepMillis > 0) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void update() {
        player.update(heldKeys, obstacles);

    int centerX = TheLastStand.getFrameWidth() / 2;
    int centerY = TheLastStand.getFrameHeight() / 2;

    for (Enemy enemy : enemies) {
        // Move towards center
        enemy.moveTowards(centerX, centerY);

        // Check collision with player
        if (enemy.getBounds().intersects(player.getBounds())) {
            lives--;                  // reduce life by 1
            enemy.respawn();          // respawn enemy after hit
            System.out.println("[Game] Player hit! Lives: " + lives);

            if (lives <= 0) {
                stopGameThread();
                System.out.println("[Game] Game Over.");
                // TODO: show game over screen
            }
        }
    }
}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        if (grassImage != null && grassImage.getWidth(null) != -1) {
            g.drawImage(grassImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(34, 139, 34));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw game ojects
        for (Obstacle obs : obstacles) obs.draw(g);
        if (player != null) player.draw(g); 
        for (Enemy enemy : enemies) enemy.draw(g);
        if (player != null) player.draw(g); // draw player on top
    }

    public GameLayeredPane getGameContainer() {
        return gameContainer;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    public int getLives() {
        return lives;
    }
    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getPlayerX() {
        return player.getX();
    }
    public int getPlayerY() {
        return player.getY();
    }
    public void setPlayerPosition(int x, int y) {
        player.setPosition(x, y);
    }
}
