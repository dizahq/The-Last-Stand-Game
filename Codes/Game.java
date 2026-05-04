package Codes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Game extends JPanel {
    private Player player;
    private GameLoop gameLoop;
    private List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
    private List<Enemy> enemies = new CopyOnWriteArrayList<>();
    private List<Bullet> bullets = new CopyOnWriteArrayList<>();
    private Powerup activePowerup = null;

    private Image grassImage;
    private Image grassOverlay; //overlay, test (NEW)
    private Image lifeFullImage;
    private Image lifeEmptyImage;
    private static final int HEART_SIZE = 60;
    private static final int HEART_PADDING = 16;
    private static final int HEART_MARGIN = 16;

    private int currentLevel;
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

    // wave display
    private int currentWave = 0;
    
    private static final long WAVE_BANNER_DURATION = 2500;
    private long waveBannerStartTime = 0;
    private boolean showWaveBanner = false;

    public Game(int panelWidth, int panelHeight, MainLayeredPane rootLayeredPane) {
        this.rootLayeredPane = rootLayeredPane;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        setLayout(null);
        setFocusable(true);

        //Game Loop
        gameLoop = new GameLoop (this);

        // Load assets (bg + lives)
        grassImage = new ImageIcon("Entities/Background/Grass BG.png").getImage();          //REPLACED
        grassOverlay = new ImageIcon("Entities/Background/BG Overlay.png").getImage();      //NEW
        lifeFullImage = new ImageIcon("Entities/UserInterface/life_Full.png").getImage();
        lifeEmptyImage = new ImageIcon("Entities/UserInterface/life_Empty.png").getImage();

        initializeWave(currentLevel);

        // Pause button
        pauseBtn.setBounds(panelWidth - 125, 20, 100, 40);
        pauseBtn.setFocusable(false);
        pauseBtn.addActionListener(e -> {
            gameLoop.pauseThread();
            rootLayeredPane.getPauseMenu().setVisible(true);
        });
        add(pauseBtn);


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

    public void update() {
        player.update(heldKeys, obstacles, activePowerup);

        int playerCX = player.getX() + 30;
        int playerCY = player.getY() + 30;

        // --- Enemy movement + player damage ---
        for (Enemy enemy : enemies) {
           boolean dealDamage = enemy.moveTowards(playerCX, playerCY, obstacles);

            if (dealDamage) {
                player.deductLife();;
                System.out.println("[Game] Player hit! Lives: " + player.getCurrentLives());
                if (player.getCurrentLives() <= 0) {
                    gameLoop.stopThread();
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
                    // Powerup drop
                    Random dropChance = new Random();
                    if(dropChance.nextInt(10) == 0 && activePowerup == null){
                        Random powerupType = new Random();
                        int powerup = powerupType.nextInt(3)+1;
                        switch (powerup) {
                            case 1:
                                activePowerup = new FireRatePowerup(enemy.getX(), enemy.getY());
                                break;
                            case 2:
                                activePowerup = new MovementSpeedPowerup(enemy.getX(), enemy.getY());
                                break;
                            case 3:
                                activePowerup = new HealPowerUp(enemy.getX(), enemy.getY());
                                break;
                        
                            default:
                                break;
                        }
                    }
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
        if(!enemiesToRemove.isEmpty()) {
            System.out.println("[Game] Enemies remaining: " + enemies.size());
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

        checkPowerup(player);
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
        if(this.activePowerup != null){
            activePowerup.draw(g);
        }

        if (player != null) {
            player.draw(g); // draw player on top (NEW)
        }

        //draw overlay (NEW)
        if (grassOverlay != null && grassOverlay.getWidth(null) != -1) {
            g.drawImage(grassOverlay, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(34, 139, 34));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (player != null) {
            drawLivesHUD(g, player); //draw GUI on top (NEW)
        }

        // Wave display
        drawWaveHUD(g);

        // Temp banner (Fades after 2.5s)
        if (showWaveBanner) {
            long elapsed = System.currentTimeMillis() - waveBannerStartTime;
            if (elapsed < WAVE_BANNER_DURATION) {
                drawWaveBanner(g, elapsed);
            } else {
                showWaveBanner = false; 
            }
        }
    }

    // Wave display counter
    private void drawWaveHUD(Graphics g) {
        String text = "Wave " + currentWave;

        g.setFont(new Font("Arial", Font.BOLD, 22));

        int textWidth = g.getFontMetrics().stringWidth(text);
        int x = (panelWidth - textWidth) / 2;
        int y = 40;

        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }

    // Wave display when new wave starts
    private void drawWaveBanner (Graphics g, long elapsed) { 
        // fades from 255 -> 0 over last 500ms duration
        float fadeStart = WAVE_BANNER_DURATION - 500f;
        int alpha = (elapsed > fadeStart) ? (int) (255 * (1f - (elapsed - fadeStart) / 500f)) : 255;
        alpha = Math.max(0, Math.min(255, alpha));

        String text = "Wave " + currentWave;
        g.setFont(new Font("Arial", Font.BOLD, 60));

        int textWidth = g.getFontMetrics().stringWidth(text);
        int x = (panelWidth - textWidth) / 2;
        int y = panelHeight / 2;

        g.setColor(new Color(255, 255, 255, alpha));
        g.drawString(text, x, y);

    }

    public void initializeWave(int currentLevel) {
        player = new Player(panelWidth / 2, panelHeight / 2, panelWidth, panelHeight, this);
        enemies.clear(); // clear leftover enemies from previous wave
        bullets.clear(); // clear leftover bullets
        currentRespawn = 0;
        spawnCount = ((currentLevel * currentLevel) + 20) / 3;

        // Increment wave 
        currentWave++;
        showWaveBanner = true;
        waveBannerStartTime = System.currentTimeMillis();
        System.out.println("[Game] Wave " + currentWave + " started.");

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
        currentLevel = 0;
        currentWave = 0;
        bullets.clear();
        enemies.clear();
        heldKeys.clear();
        initializeWave(currentLevel);
        gameLoop.startThread();
        SwingUtilities.invokeLater(()-> requestFocusInWindow());
        System.out.println("[Game] Game reset.");
    }

    public MainLayeredPane getRootLayeredPane() {
        return rootLayeredPane;
    }

    private void drawLivesHUD(Graphics g, Player player) {
        int maxLives = player.getMaxLives();
        int currentLives = player.getCurrentLives();
        for (int i = 0; i < maxLives; i++) {
            Image img = (i < currentLives) ? lifeFullImage : lifeEmptyImage;
            int heartX = HEART_MARGIN + i * (HEART_SIZE + HEART_PADDING);
            int heartY = HEART_MARGIN;
            if (img != null && img.getWidth(null) != -1) {
                g.drawImage(img, heartX, heartY, HEART_SIZE, HEART_SIZE, this);
            }
        }
    }
    // Check if powerup is past its duration
    private void checkPowerup(Player player){
        if(player.getCurrentPowerup() != null){
            Powerup currentPowerup = player.getCurrentPowerup();
            long lastPowerupTime = player.getLastPowerupTime();
            int powerupDuration = currentPowerup.getDuration();

            if(currentPowerup != null && (System.currentTimeMillis() - lastPowerupTime) > powerupDuration){
                player.resetPowerups();
            }
        }
    }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) { this.currentLevel = level; }

    public int getCurrentWave() { return currentWave; }
    public void setCurrentWave(int wave) {
        this.currentWave = wave;
    }
    
    public int getLives() { return player.getCurrentLives(); }
    public void setLives(int lives) { player.setCurrentLives(lives); }

    public int getPlayerX() { return player.getX(); }
    public int getPlayerY() { return player.getY(); }
    public void setPlayerPosition(int x, int y) { player.setPosition(x, y); }

    public void startGameThread() { gameLoop.startThread(); }
    public void pauseGameThread() { gameLoop.pauseThread(); }
    public void resumeGameThread() { gameLoop.resumeThread(); }
    public void stopGameThread() { gameLoop.stopThread(); }


    public void setActivePowerup(Powerup activePowerup) {
        this.activePowerup = activePowerup;
    }
}