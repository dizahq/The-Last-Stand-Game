package Codes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

public class Player extends GameObject {
    private static final int SPEED = 2;
    private int maxX;
    private int maxY;
    private Game game;
    private static final int ANIMATION_SPEED = 3;
    private Image currentImage;
    private int frameIndex = 0;
    private int animationTick = 0;
    private int fireRate = 500;
    private long lastFired;
    private boolean canFire = true;

    // 8 directional sprite arrays
    private Image[] walkUp, walkDown, walkLeft, walkRight;
    private Image[] walkUpRight, walkUpLeft, walkDownRight, walkDownLeft;

    Player(int x, int y, int panelWidth, int panelHeight, Game game) {
        super(x, y, 40, 60);
        this.maxX = panelWidth;
        this.maxY = panelHeight;
        this.game = game;

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
        return new Rectangle(x + 8, y + (height - 12), width - 16, 12);
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

        // 1. Determine raw input direction
        double dx = 0;
        double dy = 0;

        boolean movingUp = heldKeys.contains(KeyEvent.VK_UP);
        boolean movingDown = heldKeys.contains(KeyEvent.VK_DOWN);
        boolean movingLeft = heldKeys.contains(KeyEvent.VK_LEFT);
        boolean movingRight = heldKeys.contains(KeyEvent.VK_RIGHT);

        if (movingUp) dy -= 1;
        if (movingDown) dy += 1;
        if (movingLeft) dx -= 1;
        if (movingRight) dx += 1;

        // 2. Normalize the vector if moving
        if (dx != 0 || dy != 0) {
            // Calculate length: sqrt(x^2 + y^2)
            double length = Math.sqrt(dx * dx + dy * dy);
        
            // Divide by length to make the total magnitude 1.0, 
            // then multiply by SPEED.
            dx = (dx / length) * SPEED;
            dy = (dy / length) * SPEED;
        }   

        // 3. Apply movement with bounds checking
        this.x = (int) Math.max(0, Math.min(maxX - width, this.x + dx));
        this.y = (int) Math.max(0, Math.min(maxY - height, this.y + dy));

        // 4. Collision check (Multi-axis collision logic)
        for (Obstacle obs : obstacles) {
            if (getBounds().intersects(obs.getBounds())) {
                this.x = oldX;
                this.y = oldY;
                break;
            }
        }

        // 5. 8-directional animation logic (Keep your original logic here)
        if (movingUp && movingRight) updateAnimation(walkUpRight);
        else if (movingUp && movingLeft) updateAnimation(walkUpLeft);
        else if (movingDown && movingRight) updateAnimation(walkDownRight);
        else if (movingDown && movingLeft) updateAnimation(walkDownLeft);
        else if (movingUp) updateAnimation(walkUp);
        else if (movingDown) updateAnimation(walkDown);
        else if (movingLeft) updateAnimation(walkLeft);
        else if (movingRight) updateAnimation(walkRight);

        boolean shootUp = heldKeys.contains(KeyEvent.VK_W);
        boolean shootDown = heldKeys.contains(KeyEvent.VK_S);
        boolean shootLeft = heldKeys.contains(KeyEvent.VK_A);
        boolean shootRight = heldKeys.contains(KeyEvent.VK_D);

        int centerX = this.getX() + (width/2);
        int centerY = this.getY() + (height/2);

        if(!canFire && System.currentTimeMillis() - lastFired >= fireRate){
                canFire = true;
        }
        if(canFire){
            if (shootRight && shootUp) game.addBullet(new Bullet(centerX, centerY, Direction.NORTHWEST));
            else if (shootLeft && shootUp) game.addBullet(new Bullet(centerX, centerY, Direction.NORTHEAST));
            else if (shootRight && shootDown) game.addBullet(new Bullet(centerX, centerY, Direction.SOUTHWEST));
            else if (shootLeft && shootDown) game.addBullet(new Bullet(centerX, centerY, Direction.SOUTHEAST));
            else if (shootUp) game.addBullet(new Bullet(centerX, centerY, Direction.NORTH));
            else if (shootDown) game.addBullet(new Bullet(centerX, centerY, Direction.SOUTH));
            else if (shootRight) game.addBullet(new Bullet(centerX, centerY, Direction.WEST));
            else if (shootLeft) game.addBullet(new Bullet(centerX, centerY, Direction.EAST));
            else return;
            
            lastFired = System.currentTimeMillis();
            canFire = false;
        }
    }
    
    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}