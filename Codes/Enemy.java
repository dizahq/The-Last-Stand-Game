package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Enemy extends GameObject {
    private static final int SPEED = 1;
    private Image[] walkUp, walkDown, walkLeft, walkRight;
    private Image currentImage;
    private int panelWidth, panelHeight;
    private int frameIndex = 0;
    private int animationTick = 0;
    private static final int ANIMATION_SPEED = 5;

    public Enemy(int x, int y, int panelWidth, int panelHeight) {
        super(x, y, 60, 60); // same size as player
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        walkUp = new Image[]{
            new ImageIcon("Entities/Enemy/up1.png").getImage(),
            new ImageIcon("Entities/Enemy/up2.png").getImage(),
            new ImageIcon("Entities/Enemy/up3.png").getImage(),
            new ImageIcon("Entities/Enemy/up4.png").getImage()
        };
        walkDown = new Image[]{
            new ImageIcon("Entities/Enemy/down1.png").getImage(),
            new ImageIcon("Entities/Enemy/down2.png").getImage(),
            new ImageIcon("Entities/Enemy/down3.png").getImage(),
            new ImageIcon("Entities/Enemy/down4.png").getImage()
        };
        walkLeft = new Image[]{
            new ImageIcon("Entities/Enemy/left1.png").getImage(),
            new ImageIcon("Entities/Enemy/left2.png").getImage(),
            new ImageIcon("Entities/Enemy/left3.png").getImage(),
            new ImageIcon("Entities/Enemy/left4.png").getImage()
        };
        walkRight = new Image[]{
            new ImageIcon("Entities/Enemy/right1.png").getImage(),
            new ImageIcon("Entities/Enemy/right2.png").getImage(),
            new ImageIcon("Entities/Enemy/right3.png").getImage(),
            new ImageIcon("Entities/Enemy/right4.png").getImage()
        };

        currentImage = walkDown[0];
    }

    // Respawn at a random edge of the screen
    public void respawn() {
        int screenW = panelWidth;
        int screenH = panelHeight;

        // Pick a random side: 0=top, 1=bottom, 2=left, 3=right
        int side = (int)(Math.random() * 4);
        switch (side) {
            case 0 -> { x = (int)(Math.random() * screenW); y = 0; }
            case 1 -> { x = (int)(Math.random() * screenW); y = screenH - height; }
            case 2 -> { x = 0;          y = (int)(Math.random() * screenH); }
            case 3 -> { x = screenW - width; y = (int)(Math.random() * screenH); }
        }
    }

    // Move towards a target (player or center)
    public void moveTowards(int targetX, int targetY) {
        int centerX = this.x + this.width / 2;
        int centerY = this.y + this.height / 2;

        int dx = targetX - centerX;
        int dy = targetY - centerY;

        // Normalize direction
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;

        double moveX = (dx / dist) * SPEED;
        double moveY = (dy / dist) * SPEED;

        this.x += (int) Math.round(moveX);
        this.y += (int) Math.round(moveY);

        // Update animation based on dominant direction
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) updateAnimation(walkRight);
            else        updateAnimation(walkLeft);
        } else {
            if (dy > 0) updateAnimation(walkDown);
            else        updateAnimation(walkUp);
        }
    }

    private void updateAnimation(Image[] frames) {
        animationTick++;
        if (animationTick >= ANIMATION_SPEED) {
            animationTick = 0;
            frameIndex = (frameIndex + 1) % frames.length;
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
            // Fallback: red circle
            g.setColor(Color.RED);
            g.fillOval(x, y, width, height);
        }
    }
}