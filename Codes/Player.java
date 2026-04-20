package Codes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Player extends GameObject {
    private static final int SPEED = 2; // Constant speed to avoid re-allocating memory for variables 
    private int maxX = TheLastStand.getFrameWidth(); // Cache boundaries to avoid calling static methods every single key press
    private int maxY = TheLastStand.getFrameHeight();
    private Image [] walkUp, walkDown, walkLeft, walkRight; // Arrays for animation frames (future enhancement
    private static final int ANIMATION_SPEED = 5; // Assuming 5 frames per direction for smoother animation
    private Image currentImage; // Current image to draw (can be optimized by using a single variable instead of multiple)
    private int frameIndex = 0;   // Current frame to show
    private int animationTick = 0; // Timer to slow down the animation

    Player(int x, int y, Obstacle obstacle, JPanel gamePanel) {
        super(x, y, 60, 60);

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

    public void update(Set<Integer> heldKeys, GameObject obstacle) {
        // 1. Snapshot current position before any movement
        //    so we can revert if a collision is detected
        int oldX = this.x;
        int oldY = this.y;

        // 2. Check which keys are currently held down
        //    Using a Set allows multiple keys to be detected simultaneously, enabling smooth 8-directional movement
        boolean movingUp    = heldKeys.contains(KeyEvent.VK_W) || heldKeys.contains(KeyEvent.VK_UP);
        boolean movingDown  = heldKeys.contains(KeyEvent.VK_S) || heldKeys.contains(KeyEvent.VK_DOWN);
        boolean movingLeft  = heldKeys.contains(KeyEvent.VK_A) || heldKeys.contains(KeyEvent.VK_LEFT);
        boolean movingRight = heldKeys.contains(KeyEvent.VK_D) || heldKeys.contains(KeyEvent.VK_RIGHT);

        // 3. Apply movement independently per axis
        //    Math.max/min clamps the position within screen boundaries eparating X and Y allows diagonal movement (e.g. W+D moves up-right)
        if (movingUp)    this.y = Math.max(0,             this.y - SPEED);
        if (movingDown)  this.y = Math.min(maxY - height, this.y + SPEED);
        if (movingLeft)  this.x = Math.max(0,             this.x - SPEED);
        if (movingRight) this.x = Math.min(maxX - width,  this.x + SPEED);

        // 4. Resolve animation based on direction combo
        //    Diagonals are checked first since they're more specific; horizontal facing is preferred for diagonal sprites
        if      (movingUp   && movingRight) updateAnimation(walkRight);
        else if (movingUp   && movingLeft)  updateAnimation(walkLeft);
        else if (movingDown && movingRight) updateAnimation(walkRight);
        else if (movingDown && movingLeft)  updateAnimation(walkLeft);
        else if (movingUp)                  updateAnimation(walkUp);
        else if (movingDown)                updateAnimation(walkDown);
        else if (movingLeft)                updateAnimation(walkLeft);
        else if (movingRight)               updateAnimation(walkRight);
        // No else — if no key is held, currentImage stays on the last frame (idle pose)

        // 5. Collision check — if the player now overlaps the obstacle,
        //    revert both axes to the pre-move snapshot.
        //    This handles all 8 directions without needing per-axis collision logic
        if (this.getBounds().intersects(obstacle.getBounds())) {
            this.x = oldX;
            this.y = oldY;
        }
    }
}