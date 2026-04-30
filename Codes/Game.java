package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Game extends JPanel implements Runnable {
    private Thread gameThread;
    private volatile boolean running = false;
    private volatile boolean paused = false;

    // 60 frames per sec
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1_000_000_000L / TARGET_FPS;

    private Player player;
    private List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
    private List<Enemy> enemies = new CopyOnWriteArrayList<>();
    private List<Bullet> bullets = new CopyOnWriteArrayList<>();

    private Image grassImage;
    private Image lifeFullImage;
    private Image lifeEmptyImage;
    private static final int MAX_LIVES = 4;
    private static final int HEART_SIZE = 60;
    private static final int HEART_PADDING = 16;
    private static final int HEART_MARGIN = 16;

    private int currentLevel;
    private int lives = 4;

    private int panelWidth, panelHeight;

    private final Set<Integer> heldKeys = java.util.Collections.synchronizedSet(new HashSet<>());

    private MainLayeredPane rootLayeredPane;
    private JButton pauseBtn = new JButton("Pause");

    // spawning parameters
    private double lastEnemySpawnTime;
    private int spawnCount;
    private int spawnRate = 5000;
    private int currentRespawn = 0;
    private int respawns = 3;

    public Game(int panelWidth, int panelHeight, MainLayeredPane rootLayeredPane) {
        this.rootLayeredPane = rootLayeredPane;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        setLayout(null);

        // Load assets (bg + lives)
        grassImage = new ImageIcon("Entities/Background/grass.png").getImage();
        lifeFullImage = new ImageIcon("Entities/UserInterface/life_Full.png").getImage();
        lifeEmptyImage = new ImageIcon("Entities/UserInterface/life_Empty.png").getImage();

        initializeWave(currentLevel);

        // Pause button
        pauseBtn.setBounds(panelWidth - 125, 20, 100, 40);
        pauseBtn.setFocusable(false);
        pauseBtn.addActionListener(e -> {
            pauseGameThread();
            rootLayeredPane.getPauseMenu().setVisible(true);
        });
        add(pauseBtn);

        // TEMP: Game over test button - will remove once everything's set
        JButton testGameOverBtn = new JButton("Test Game Over");
        testGameOverBtn.setBounds(panelWidth - 125, 70, 100, 40);
        testGameOverBtn.setFocusable(false);
        testGameOverBtn.addActionListener(e -> {
            stopGameThread();
            SwingUtilities.invokeLater(() -> rootLayeredPane.getGameOver().setVisible(true));
        });
        add(testGameOverBtn);

        // Key listeners
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

        SwingUtilities.invokeLater(()-> requestFocusInWindow());

        System.out.println("[Game] Game loop starts.");
    }

    // Pause loop
    public void pauseGameThread() {
        paused = true;
        System.out.println("[Game] Game paused.");
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

    // Permanently stops game loop
    public void stopGameThread() {
        running = false;
        paused = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }
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

            // Sleep for remaining time in frame
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

        int playerCX = player.getX() + 20;
        int playerCY = player.getY() + 30;

        // --- Enemy movement + player damage ---
        for (Enemy enemy : enemies) {
           boolean dealDamage = enemy.moveTowards(playerCX, playerCY, obstacles);

            if (dealDamage) {
                lives--;
                System.out.println("[Game] Player hit! Lives: " + lives);
                if (lives <= 0) {
                    stopGameThread();
                    SwingUtilities.invokeLater(() ->
                        rootLayeredPane.getGameOver().setVisible(true)
                    );
                    return;
                }
            }
        }

        // --- Bullet update + collision ---
        // Collect removals first, apply after — never remove during iteration on CopyOnWriteArrayList
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            bullet.update();

            // Remove bullet if off-screen
            if (bullet.getX() < 0 || bullet.getX() > panelWidth ||
                bullet.getY() < 0 || bullet.getY() > panelHeight) {
                bulletsToRemove.add(bullet);
                System.out.println("[Game] Bullet removed (off-screen). Remaining: " + (bullets.size() - bulletsToRemove.size()));
                continue;
            }

            // Bullet vs enemy collision
            for (Enemy enemy : enemies) {
                if (!enemiesToRemove.contains(enemy) && bullet.getBounds().intersects(enemy.getBounds())) {
                    bulletsToRemove.add(bullet);
                    enemiesToRemove.add(enemy);
                    System.out.println("[Game] Enemy hit by bullet!");
                    break;
                }
            }
        }

        // Safe bulk removal after all iteration is done
        bullets.removeAll(bulletsToRemove);
        enemies.removeAll(enemiesToRemove);

        if (!bulletsToRemove.isEmpty()) {
            System.out.println("[Game] Bullets remaining: " + bullets.size());
        }

        // --- Enemy respawn waves ---
        while (currentRespawn < respawns) {
            if (System.currentTimeMillis() - lastEnemySpawnTime > spawnRate) {
                spawnEnemies(spawnCount);
            } else {
                break;
            }
        }

        // --- Level up when all waves cleared and no enemies left ---
        if (currentRespawn == respawns && enemies.isEmpty()) {
            currentLevel++;
            System.out.println("[Game] Level Up! Current Level: " + currentLevel);
            initializeWave(currentLevel);
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

        // Draw game objects
        for (Obstacle obs : obstacles) obs.draw(g);
        for (Bullet bullet : bullets) bullet.draw(g);
        for (Enemy enemy : enemies) enemy.draw(g);
        if (player != null) player.draw(g); // draw player on top
        drawLivesHUD(g);
    }

    public void initializeWave(int currentLevel) {
        player = new Player(panelWidth / 2, panelHeight / 2, panelWidth, panelHeight, this);
        enemies.clear(); // clear leftover enemies from previous wave
        bullets.clear(); // clear leftover bullets
        currentRespawn = 0;
        spawnCount = ((currentLevel * currentLevel) + 20) / 3;
        spawnEnemies(spawnCount);
    }

    public void spawnEnemies(int enemyCount) {
        for (int i = 0; i < enemyCount; i++) {
            Enemy e = new Enemy(0, 0, panelWidth, panelHeight);
            e.respawn(); // random edge position
            enemies.add(e);
        }
        lastEnemySpawnTime = System.currentTimeMillis();
        currentRespawn++;
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void resetGame() {
        lives = 4;
        currentLevel = 0;
        bullets.clear();
        enemies.clear();
        heldKeys.clear();
        initializeWave(currentLevel);
        SwingUtilities.invokeLater(()-> requestFocusInWindow());
        System.out.println("[Game] Game reset.");
    }

    public MainLayeredPane getRootLayeredPane() {
        return rootLayeredPane;
    }

    private void drawLivesHUD(Graphics g) {
        for (int i = 0; i < MAX_LIVES; i++) {
            Image img = (i < lives) ? lifeFullImage : lifeEmptyImage;
            int heartX = HEART_MARGIN + i * (HEART_SIZE + HEART_PADDING);
            int heartY = HEART_MARGIN;
            if (img != null && img.getWidth(null) != -1) {
                g.drawImage(img, heartX, heartY, HEART_SIZE, HEART_SIZE, this);
            }
        }
    }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) { this.currentLevel = level; }

    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }

    public int getPlayerX() { return player.getX(); }
    public int getPlayerY() { return player.getY(); }
    public void setPlayerPosition(int x, int y) { player.setPosition(x, y); }
}