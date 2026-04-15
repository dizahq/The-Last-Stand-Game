package Codes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;

//Inherits from GameObject to use basic positioning and collision logic.
public class Player extends GameObject {
    private JPanel gamePanel; // Reference to the JPanel to trigger visual updates
    private Wall obstacle;    // The specific wall object the player interacts with
    
    // Constructor to initialize position, the obstacle, and the panel reference
    Player(int x, int y, Wall obstacle, JPanel gamePanel) {
        super(x, y, 40, 40); // Sets player size to 40x40 pixels via the parent class
        this.obstacle = obstacle;
        this.gamePanel = gamePanel;
    }

    @Override
    public void draw(Graphics g) {
        // Set the brush color to Blue and draw the player as a filled rectangle
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }

    // Processes keyboard input to move the player and check for collisions.
    public void handleKeyPress(KeyEvent e, GameObject obstacle) {
        int key = e.getKeyCode();
        int speed = 10;
        char keyChar = e.getKeyChar();

        // Define the boundaries of your game window
        int minX = 0;
        int minY = 0;
        int maxX = 500-12; // Panel width (e.g., 400)
        int maxY = 500-35; // Panel height (e.g., 400)

        // Movement with Boundary Checks
        // UP: Only move if y is greater than the top bound
        if ((key == KeyEvent.VK_W || key == KeyEvent.VK_UP || keyChar == 'w') && this.y > minY) this.y -= speed;

        // DOWN: Only move if y + height is less than the bottom bound
        if ((key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN || keyChar == 's') && (this.y + this.height) < maxY) this.y += speed;

        // LEFT: Only move if x is greater than the left bound
        if ((key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT || keyChar == 'a') && this.x > minX) this.x -= speed;

        // RIGHT: Only move if x + width is less than the right bound
        if ((key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT || keyChar == 'd') && (this.x + this.width) < maxX) this.x += speed;

        // Collision logic for the wall obstacle
        if (this.intersects(obstacle)) {
            System.out.println("Hit! Closing...");
            System.exit(0);
        }
    }
}