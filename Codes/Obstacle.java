package Codes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

//Represents a static obstacle that the player must avoid.
public class Obstacle extends GameObject {
    private int panelWidth, panelHeight;
    private Image image; // Optional: If you want to use an image instead of a solid color  
    
    // Constructor passing coordinates and dimensions to the GameObject parent.
    Obstacle(int x, int y, int width, int height, int panelWidth, int panelHeight) {
        super(x, y, width, height);
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        image = new ImageIcon("Entities/Object/rock1.png").getImage();
        
        // Optional: Safety clamp to ensure wall doesn't exceed screen bounds 
        // using the dynamic frame sizes from your main class.
        int maxX = panelWidth;
        int maxY = panelHeight;
        if (this.x + this.width > maxX) this.width = maxX - this.x;
        if (this.y + this.height > maxY) this.height = maxY - this.y;
    }

    @Override
    public Rectangle getBounds() {
        // Tight oval-ish box that hugs the visible rock, ignoring transparent padding
        int margin = (int)(Math.min(width, height) * 0.25); // 25% inset on all sides
        return new Rectangle(
            x + margin,
            y + margin,
            width  - margin * 2,
            height - margin * 2
        );
    }
    

    // Renders the wall as a Red rectangle on the screen.
    @Override
    public void draw(Graphics g) {
       // 2. Draw the sprite if it loaded correctly
        if (image != null && image.getWidth(null) != -1) {
            // Draws the image scaled to the width and height of the GameObject
            g.drawImage(image, x, y, width, height, null);
        } else {
            // Fallback: If the image is missing, draw the red box so the game doesn't break
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
            
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
        }
    }
}