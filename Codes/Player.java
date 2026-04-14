package Codes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;

public class Player extends GameObject{
    private JPanel gamePanel; // Reference to refresh the screen
    private Wall obstacle;
    
    Player(int x, int y,  Wall obstacle, JPanel gamePanel) {
        super(x, y, 40, 40);
        this.obstacle = obstacle;
        this.gamePanel = gamePanel;
    }

   @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }

   public void handleKeyPress(KeyEvent e, GameObject obstacle) {
        int key = e.getKeyCode();
        int speed = 10;

        if (key == KeyEvent.VK_W) this.y -= speed;
        if (key == KeyEvent.VK_S) this.y += speed;
        if (key == KeyEvent.VK_A) this.x -= speed;
        if (key == KeyEvent.VK_D) this.x += speed;

        // Collision logic inside the player class
        if (this.intersects(obstacle)) {
            System.out.println("Hit! Closing...");
            System.exit(0);
        }
    }
    
}
