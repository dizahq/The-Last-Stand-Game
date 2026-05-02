package Codes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;

<<<<<<< Updated upstream
import javax.swing.ImageIcon;

public class Player extends GameObject {
    private int speed = 2;
    private int maxLives = 4;
    private int currentLives;
    private Powerup currentPowerup = null;
    private long lastPowerupTime;
    private int maxX;
    private int maxY;
    private Game game;
    private static final int ANIMATION_SPEED = 8;
    private Image currentImage;

    // Attack sprites for each direction
    private Image attackUp, attackDown, attackLeft, attackRight;
    private Image attackUpRight, attackUpLeft, attackDownRight, attackDownLeft;

    // The currently active attack sprite
    private Image currentAttack;
    private boolean showingAttack = false;
    private long attackStartTime;
    private static final int ATTACK_DURATION = 120;

    private int frameIndex = 0;
    private int animationTick = 0;
=======
public class Player extends Entity {
    private static final int SPEED = 2;
    private int maxX;
    private int maxY;
    private Game game;
    private static final int ANIMATION_SPEED = 3;
>>>>>>> Stashed changes
    private int fireRate = 500;
    private long lastFired;
    private boolean canFire = true;
    private Image[] lastStrip = null;

    // 8 directional sprite arrays
    private static Image[] walkUp, walkDown, walkLeft, walkRight;
    private static Image[] walkUpRight, walkUpLeft, walkDownRight, walkDownLeft;

    Player(int x, int y, int panelWidth, int panelHeight, Game game) {
        super(x, y, 40, 60);
        this.maxX = panelWidth;
        this.maxY = panelHeight;
        this.game = game;
        this.currentLives = 4;

        if (walkDown == null){
            walkUp = loadStrip("Entities/Player/Normal/up",                  4);
            walkDown = loadStrip("Entities/Player/Normal/down",                 4);
            walkLeft = loadStrip("Entities/Player/Normal/left",                 4);
            walkRight = loadStrip("Entities/Player/Normal/right",                4);
            walkUpRight = loadStrip("Entities/Player/Normal/upperdiagonal_right",  4);
            walkUpLeft = loadStrip("Entities/Player/Normal/upperdiagonal_left",   4);
            walkDownRight = loadStrip("Entities/Player/Normal/lowerdiagonal_right",  4);
            walkDownLeft  = loadStrip("Entities/Player/Normal/lowerdiagonal_left",   4);
        }

        currentImage = walkDown[0]; // default idle frame

        // Load attack effect
        // Cardinal attack sprites
        attackUp = new ImageIcon("Entities/Player/Attack/atk_up1.png").getImage();
        attackDown = new ImageIcon("Entities/Player/Attack/atk_down1.png").getImage();
        attackLeft = new ImageIcon("Entities/Player/Attack/atk_left1.png").getImage();
        attackRight = new ImageIcon("Entities/Player/Attack/atk_right1.png").getImage();

        // Diagonal attack sprites
        attackUpRight = new ImageIcon("Entities/Player/Attack/atk_upRight1.png").getImage();
        attackUpLeft = new ImageIcon("Entities/Player/Attack/atk_upLeft1.png").getImage();
        attackDownRight = new ImageIcon("Entities/Player/Attack/atk_downRight1.png").getImage();
        attackDownLeft = new ImageIcon("Entities/Player/Attack/atk_downLeft1.png").getImage();

    }

    private void updateAnimation(Image[] frames) {
        // reset if direction changed
        if (frames != lastStrip) {
            frameIndex = 0;
            animationTick = 0;
            lastStrip = frames;
        }

        animationTick++;
        if (animationTick >= ANIMATION_SPEED) {
            animationTick = 0;
            frameIndex++;
            if (frameIndex >= frames.length) frameIndex = 0;
        }

        currentImage = frames[frameIndex];
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x + 8, y + (height - 12), width - 16, 12);
    }

    @Override
    public void draw(Graphics g) {
        Image spriteToDraw;

        if (showingAttack && currentAttack != null) {
            spriteToDraw = currentAttack;
        } else{
            spriteToDraw = currentImage;
        }

        if (spriteToDraw != null && spriteToDraw.getWidth(null) != -1) {
            g.drawImage(spriteToDraw, x, y, width, height, null);
        } else {
            g.setColor(java.awt.Color.BLUE);
            g.fillRect(x, y, width, height);
        }
    }

    public void update(Set<Integer> heldKeys, List<Obstacle> obstacles, Powerup powerup) {
        int oldX = this.x;
        int oldY = this.y;

        // Turn off attack effect after duration expires (NEW)
        if (showingAttack &&
            System.currentTimeMillis() - attackStartTime >= ATTACK_DURATION) {
            showingAttack = false;
        }

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
            // then multiply by speed.
            dx = (dx / length) * speed;
            dy = (dy / length) * speed;
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
        
        if (powerup != null && getBounds().intersects(powerup.getBounds())){
            powerup.applyEffect(this);
            this.currentPowerup = powerup;
            game.setActivePowerup(null);
            lastPowerupTime = System.currentTimeMillis();
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

        //Shoot bullet
        if(canFire){
            if (shootRight && shootUp) {
                game.addBullet(new Bullet(centerX, centerY, Direction.NORTHWEST));
                currentAttack = attackUpRight;
            }else if (shootLeft && shootUp) {
                game.addBullet(new Bullet(centerX, centerY, Direction.NORTHEAST));
                currentAttack = attackUpLeft;
            }else if (shootRight && shootDown) {
                game.addBullet(new Bullet(centerX, centerY, Direction.SOUTHWEST));
                currentAttack = attackDownRight;
            }else if (shootLeft && shootDown) {
                game.addBullet(new Bullet(centerX, centerY, Direction.SOUTHEAST));
                currentAttack = attackDownLeft;
            }else if (shootUp) {
                game.addBullet(new Bullet(centerX, centerY, Direction.NORTH));
                currentAttack = attackUp;
            }else if (shootDown) {
                game.addBullet(new Bullet(centerX, centerY, Direction.SOUTH));
                currentAttack = attackDown;
            }else if (shootRight) {
                game.addBullet(new Bullet(centerX, centerY, Direction.WEST));
                currentAttack = attackRight;
            }else if (shootLeft) {
                game.addBullet(new Bullet(centerX, centerY, Direction.EAST));
                currentAttack = attackLeft;
            }else return;

            lastFired = System.currentTimeMillis();
            canFire = false;

            // Show attack effect
            showingAttack = true;
            attackStartTime = System.currentTimeMillis();
        }
    }

    public void addLife(){
        currentLives++;
    }

    public void deductLife(){
        currentLives--;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getMaxLives() { return maxLives; }
    public int getCurrentLives() { return currentLives; }
    public Powerup getCurrentPowerup() { return currentPowerup; }
    public long getLastPowerupTime() { return lastPowerupTime; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCurrentLives(int currentLives) {
        this.currentLives = currentLives;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void resetPowerups(){
        currentPowerup = null;
        this.speed = 2;
        this.fireRate = 500;
    }
}
