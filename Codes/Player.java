package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Player extends GameObject {
    // Constant speed to avoid re-allocating memory for variables
    private static final int SPEED = 5;

    // Cache boundaries to avoid calling static methods every single key press
    private int maxX = TheLastStand.getFrameWidth();
    private int maxY = TheLastStand.getFrameHeight();
    private Image upPlayer, downPlayer, leftPlayer, rightPlayer, currentImage;

    Player(int x, int y, Obstacle obstacle, JPanel gamePanel) {
        super(x, y, 40, 40);

        upPlayer = new ImageIcon("C:\\Users\\dizah\\Documents\\GitHub\\The-Last-Stand-Game\\Entities\\upPlayer.png").getImage();
        downPlayer = new ImageIcon("C:\\Users\\dizah\\Documents\\GitHub\\The-Last-Stand-Game\\Entities\\downPlayer.png").getImage();
        leftPlayer = new ImageIcon("C:\\Users\\dizah\\Documents\\GitHub\\The-Last-Stand-Game\\Entities\\leftPlayer.png").getImage();
        rightPlayer = new ImageIcon("C:\\Users\\dizah\\Documents\\GitHub\\The-Last-Stand-Game\\Entities\\rightPlayer.png").getImage();
        currentImage = new ImageIcon("C:\\Users\\dizah\\Documents\\GitHub\\The-Last-Stand-Game\\Entities\\downPlayer.png").getImage();

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
            currentImage = upPlayer;
        }

        //DOWN
        else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN || keyChar == 's') {
            this.y = Math.min(maxY - height, this.y + SPEED);
            currentImage = downPlayer;
        }
        // LEFT
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT || keyChar == 'a') {
            this.x = Math.max(0, this.x - SPEED);
            currentImage = leftPlayer;
        } 
        // RIGHT
        else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT || keyChar == 'd') {
            this.x = Math.min(maxX - width, this.x + SPEED);
            currentImage = rightPlayer;
        }

        // 3. Collision Check
        // Only revert if a change actually happened and an intersection exists
        if ((this.x != oldX || this.y != oldY) && this.intersects(obstacle)) {
            this.x = oldX;
            this.y = oldY;
        }
    }
}