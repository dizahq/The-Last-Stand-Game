package Codes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Player extends GameObject {
    private static final int SPEED = 2;
    private int maxX;
    private int maxY;
    private static final int ANIMATION_SPEED = 3;
    private Image currentImage;
    private int frameIndex = 0;
    private int animationTick = 0;

    // 8 directional sprite arrays
    private Image[] walkUp, walkDown, walkLeft, walkRight;
    private Image[] walkUpRight, walkUpLeft, walkDownRight, walkDownLeft;

    Player(int x, int y, int panelWidth, int panelHeight, List<Obstacle> obstacles, JPanel gamePanel) {
        super(x, y, 40, 60);
        this.maxX = panelWidth;
        this.maxY = panelHeight;

        // Cardinal directions
        walkUp = new Image[]{
            new ImageIcon("Entities/Player/Normal/up1.png").getImage(),
            new ImageIcon("Entities/Player/Normal/up2.png").getImage(),
            new ImageIcon("Entities/Player/Normal/up3.png").getImage(),
            new ImageIcon("Entities/Player/Normal/up4.png").getImage()
        };
        walkDown = new Image[]{
            new ImageIcon("Entities/Player/Normal/down1.png").getImage(),
            new ImageIcon("Entities/Player/Normal/down2.png").getImage(),
            new ImageIcon("Entities/Player/Normal/down3.png").getImage(),
            new ImageIcon("Entities/Player/Normal/down4.png").getImage()
        };
        walkLeft = new Image[]{
            new ImageIcon("Entities/Player/Normal/left1.png").getImage(),
            new ImageIcon("Entities/Player/Normal/left2.png").getImage(),
            new ImageIcon("Entities/Player/Normal/left3.png").getImage(),
            new ImageIcon("Entities/Player/Normal/left4.png").getImage()
        };
        walkRight = new Image[]{
            new ImageIcon("Entities/Player/Normal/right1.png").getImage(),
            new ImageIcon("Entities/Player/Normal/right2.png").getImage(),
            new ImageIcon("Entities/Player/Normal/right3.png").getImage(),
            new ImageIcon("Entities/Player/Normal/right4.png").getImage()
        };

        // Diagonal directions
        walkUpRight = new Image[]{
            new ImageIcon("Entities/Player/Normal/upperdiagonal_right1.png").getImage(),
            new ImageIcon("Entities/Player/Normal/upperdiagonal_right2.png").getImage(),
            new ImageIcon("Entities/Player/Normal/upperdiagonal_right3.png").getImage(),
            new ImageIcon("Entities/Player/Normal/upperdiagonal_right4.png").getImage()
        };
        walkUpLeft = new Image[]{
            new ImageIcon("Entities/Player/Normal/upperdiagonal_left1.png").getImage(),
            new ImageIcon("Entities/Player/Normal/upperdiagonal_left2.png").getImage(),
            new ImageIcon("Entities/Player/Normal/upperdiagonal_left3.png").getImage(),
            new ImageIcon("Entities/Player/Normal/upperdiagonal_left4.png").getImage()
        };
        walkDownRight = new Image[]{
            new ImageIcon("Entities/Player/Normal/lowerdiagonal_right1.png").getImage(),
            new ImageIcon("Entities/Player/Normal/lowerdiagonal_right2.png").getImage(),
            new ImageIcon("Entities/Player/Normal/lowerdiagonal_right3.png").getImage(),
            new ImageIcon("Entities/Player/Normal/lowerdiagonal_right4.png").getImage()
        };
        walkDownLeft = new Image[]{
            new ImageIcon("Entities/Player/Normal/lowerdiagonal_left1.png").getImage(),
            new ImageIcon("Entities/Player/Normal/lowerdiagonal_left2.png").getImage(),
            new ImageIcon("Entities/Player/Normal/lowerdiagonal_left3.png").getImage(),
            new ImageIcon("Entities/Player/Normal/lowerdiagonal_left4.png").getImage()
        };

        currentImage = walkDown[0]; // default idle frame
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
        int hbWidth = width - 16;
        int hbHeight = 12;
        int offsetX = x + 8;
        int offsetY = y + (height - hbHeight);
        return new Rectangle(offsetX, offsetY, hbWidth, hbHeight);
    }

    @Override
    public void draw(Graphics g) {
        if (currentImage != null && currentImage.getWidth(null) != -1) {
            g.drawImage(currentImage, x, y, width, height, null);
        } else {
            g.setColor(java.awt.Color.BLUE);
            g.fillRect(x, y, width, height);
        }
    }

    public void update(Set<Integer> heldKeys, List<Obstacle> obstacles) {
        int oldX = this.x;
        int oldY = this.y;

        boolean movingUp = heldKeys.contains(KeyEvent.VK_W) || heldKeys.contains(KeyEvent.VK_UP);
        boolean movingDown  = heldKeys.contains(KeyEvent.VK_S) || heldKeys.contains(KeyEvent.VK_DOWN);
        boolean movingLeft  = heldKeys.contains(KeyEvent.VK_A) || heldKeys.contains(KeyEvent.VK_LEFT);
        boolean movingRight = heldKeys.contains(KeyEvent.VK_D) || heldKeys.contains(KeyEvent.VK_RIGHT);

        if (movingUp) this.y = Math.max(0, this.y - SPEED);
        if (movingDown)  this.y = Math.min(maxY - height, this.y + SPEED);
        if (movingLeft)  this.x = Math.max(0, this.x - SPEED);
        if (movingRight) this.x = Math.min(maxX - width, this.x + SPEED);

        // 8-directional animation
        if (movingUp && movingRight) updateAnimation(walkUpRight);   // ↗
        else if (movingUp && movingLeft) updateAnimation(walkUpLeft);    // ↖
        else if (movingDown && movingRight) updateAnimation(walkDownRight); // ↘
        else if (movingDown && movingLeft) updateAnimation(walkDownLeft);  // ↙
        else if (movingUp) updateAnimation(walkUp);        // ↑
        else if (movingDown) updateAnimation(walkDown);      // ↓
        else if (movingLeft) updateAnimation(walkLeft);      // ←
        else if (movingRight) updateAnimation(walkRight);     // →

        // Collision check
        for (Obstacle obs : obstacles) {
            if (getBounds().intersects(obs.getBounds())) {
                this.x = oldX;
                this.y = oldY;
                break;
            }
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}