package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
    private int panelWidth, panelHeight;
    private MainLayeredPane rootLayeredPane;
    private Player player;
    private Obstacle obstacle;
    private Image grassImage;
    private int currentLevel;
    
    private JButton pauseBtn = new JButton("Pause");

    public GamePanel(int panelWidth, int panelHeight, MainLayeredPane rootLayeredPane) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.rootLayeredPane = rootLayeredPane;
        
        // 1. Setup Panel properties
        setLayout(null); // Allows absolute positioning for the button
        
        // 2. Load Assets
        // Note: Using your absolute path as requested
        grassImage = new ImageIcon("Entities/Background/grass.png").getImage();
        
        // 3. Initialize Objects
        obstacle = new Obstacle(200, 200, 50, 50, panelWidth, panelHeight);
        player = new Player(300, 100, panelWidth, panelHeight, obstacle, this);

        // 4. Setup Pause Button
        pauseBtn.setBounds(10, 10, 80, 30);
        pauseBtn.addActionListener(e -> {
            pause();
        });
        add(pauseBtn);

        // 5. Movement Handling
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Pass movement and collision logic to the player object
                player.handleKeyPress(e, obstacle);
                // Redraw the panel to show the new position
                repaint();
            }
        });

        // 6. FORCE FOCUS ON LOAD
        // This listener ensures that as soon as this panel is shown on screen,
        // it grabs the keyboard focus so the player can move immediately.
        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                requestFocusInWindow();
            }
        });
    }

    @Override
    public void run(){

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Important for clearing previous frames
        
        // 1. Draw Background (Grass)
        if (grassImage != null && grassImage.getWidth(null) != -1) {
            g.drawImage(grassImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback color if the image fails to load
            g.setColor(new Color(34, 139, 34));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Draw Game Entities (On top of background)
        if (obstacle != null) {
            obstacle.draw(g);
        }
        
        if (player != null) {
            player.draw(g);
        }
    }

    public void pause(){
        // pause thread here
        setFocusable(false);
        rootLayeredPane.getPauseMenu().setVisible(true);
    }
    // --- Getters and Setters ---
    
    public int getCurrentLevel() { 
        return currentLevel; 
    }
    
    public void setCurrentLevel(int currentLevel) { 
        this.currentLevel = currentLevel; 
    }
}