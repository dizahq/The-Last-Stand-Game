package Codes;

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
import javax.swing.Timer;

public class GamePanel extends JPanel {
    private GameLayeredPane gameContainer;
    private Player player;
    private Obstacle obstacle;
    private Image grassImage;
    private int panelWidth = TheLastStand.getFrameWidth();
    private int panelHeight = TheLastStand.getFrameHeight();
    private int currentLevel;

    // --- Key state tracking for 8-directional movement ---
    private final Set<Integer> heldKeys = new HashSet<>();
    private Timer gameLoop;

    private JButton pauseBtn = new JButton("Pause");

    public GamePanel(GameLayeredPane gameContainer) {
        this.gameContainer = gameContainer;

        // 1. Setup Panel properties
        setBounds(0, 0, panelWidth, panelHeight);
        setLayout(null);
        setFocusable(true);

        // 2. Load Assets
        grassImage = new ImageIcon("C:\\CMSC 12\\JAVA\\The-Last-Stand-Game\\Entities\\Background\\grass.png").getImage();

        // 3. Initialize Objects
        obstacle = new Obstacle(200, 200, 100, 100);
        player = new Player(300, 100, obstacle, this);

        // 4. Setup Pause Button
        pauseBtn.setBounds(TheLastStand.getFrameWidth() - 125, 20, 100, 40);
        pauseBtn.setFocusable(false);
        pauseBtn.addActionListener(e -> {
            gameLoop.stop(); // Pause the game loop too
            getGameContainer().getPauseMenu().setVisible(true);
        });
        add(pauseBtn);

        // 5. Key listeners — track held keys instead of reacting per-press
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

        // 6. Game loop — runs at ~60fps, updates player and redraws
        gameLoop = new Timer(16, e -> {
            player.update(heldKeys, obstacle);
            repaint();
        });
        gameLoop.start();

        // 7. Force focus on load
        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                requestFocusInWindow();
            }
        });
    }

    // Call this when resuming from pause
    public void resumeGameLoop() {
        gameLoop.start();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (grassImage != null && grassImage.getWidth(null) != -1) {
            g.drawImage(grassImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(34, 139, 34));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (obstacle != null) obstacle.draw(g);
        if (player != null) player.draw(g);
    }

    public GameLayeredPane getGameContainer() { return gameContainer; }
    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
}