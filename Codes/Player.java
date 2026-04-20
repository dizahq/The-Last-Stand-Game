package Codes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Player extends GameObject {
    private static final int SPEED = 5; // Constant speed to avoid re-allocating memory for variables 
    private int maxX; // Cache boundaries to avoid calling static methods every single key press
    private int maxY;
    private Image [] walkUp, walkDown, walkLeft, walkRight; // Arrays for animation frames (future enhancement
    private static final int ANIMATION_SPEED = 5; // Assuming 5 frames per direction for smoother animation
    private Image currentImage; // Current image to draw (can be optimized by using a single variable instead of multiple)
    private int frameIndex = 0;   // Current frame to show
    private int animationTick = 0; // Timer to slow down the animation

    Player(int x, int y, int panelWidth, int panelHeight, Obstacle obstacle, JPanel gamePanel) {
        super(x, y, 40, 40);
        this.maxX = panelWidth;
        this.maxY = panelHeight;

        //Load arrays of images for animation (currently not used, but set up for future enhancement)
        walkUp = new Image[]{
            new ImageIcon("Entities/Player/up1.png").getImage(),
            new ImageIcon("Entities/Player/up2.png").getImage(),
            new ImageIcon("Entities/Player/up3.png").getImage(),
            new ImageIcon("Entities/Player/up4.png").getImage()
        };

        walkDown = new Image[]{
            new ImageIcon("Entities/Player/down1.png").getImage(),
            new ImageIcon("Entities/Player/down2.png").getImage(),
            new ImageIcon("Entities/Player/down3.png").getImage(),
            new ImageIcon("Entities/Player/down4.png").getImage()
        };

        walkRight = new Image[]{
            new ImageIcon("Entities/Player/right1.png").getImage(),
            new ImageIcon("Entities/Player/right2.png").getImage(),
            new ImageIcon("Entities/Player/right3.png").getImage(),
            new ImageIcon("Entities/Player/right4.png").getImage()
        };

        walkLeft = new Image[]{
            new ImageIcon("Entities/Player/left1.png").getImage(),
            new ImageIcon("Entities/Player/left2.png").getImage(),
            new ImageIcon("Entities/Player/left3.png").getImage(),
            new ImageIcon("Entities/Player/left4.png").getImage()
        };
        currentImage = walkDown[0]; // Start with the first frame of walking down as the default image
    }

    private void updateAnimation(Image[] frames) {
        animationTick++;
        if (animationTick >= ANIMATION_SPEED) {
            animationTick = 0;
            frameIndex++;
            if (frameIndex >= frames.length) {
                frameIndex = 0;
            }
        }
        currentImage = frames[frameIndex];
    }

    @Override
    public Rectangle getBounds() {
        // We make the hitbox 20px wide and 10px tall, positioned at the feet
        int hbWidth = width - 16;
        int hbHeight = 12;
        int offsetX = x + 8; // Centers it
        int offsetY = y + (height - hbHeight);   // At the very bottom
    
        return new Rectangle(offsetX, offsetY, hbWidth, hbHeight);
    }

   @Override
    public void draw(Graphics g) {
        if (currentImage != null && currentImage.getWidth(null) != -1) {
            g.drawImage(currentImage, x, y, width, height, null);
        } else {
           g.drawImage(currentImage, x, y, width, height, null);
        }
    }

    public void handleKeyPress(KeyEvent e, GameObject obstacle) {
        int key = e.getKeyCode();
        char keyChar = e.getKeyChar();

        // 1. Store current position in local primitives (stack memory is faster)
        int oldX = this.x;
        int oldY = this.y;

        // 2. Optimized Movement Logic
        // Combine WASD and Arrows into single checks to reduce branching
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || keyChar == 'w') {
            this.y = Math.max(0, this.y - SPEED);
            updateAnimation(walkUp);
        }

        //DOWN
        else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN || keyChar == 's') {
            this.y = Math.min(maxY - height, this.y + SPEED);
            updateAnimation(walkDown);
        }
        // LEFT
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT || keyChar == 'a') {
            this.x = Math.max(0, this.x - SPEED);
            updateAnimation(walkLeft);
        } 
        // RIGHT
        else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT || keyChar == 'd') {
            this.x = Math.min(maxX - width, this.x + SPEED);
            updateAnimation(walkRight);
        }

        // 3. Collision Check
        // Only revert if a change actually happened and an intersection exists
        if (this.getBounds().intersects(obstacle.getBounds())) {
            this.x = oldX;
            this.y = oldY;
        }
    }

    //C:\Users\dizah\Documents\GitHub\The-Last-Stand-Game\Codes
}